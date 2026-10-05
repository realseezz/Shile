package com.realseezz.shile.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.realseezz.shile.common.ShileState;
import com.realseezz.shile.common.network.MiniGameEndPacket;
import com.realseezz.shile.common.network.ShileSpawnedPacket;
import com.realseezz.shile.server.ShileManager;
import com.realseezz.shile.server.ShilePlayerState;

/**
 * /shile komut ağacı. Komutlar server durumunu değiştirir ve client'a paket gönderir.
 */
public final class ShileCommands {
	private ShileCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			dispatcher.register(Commands.literal("shile")
				// Yetki: operator (cheat acik tek oyunculu dunyada host dahil).
				.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
				.executes(ShileCommands::help)
				.then(Commands.literal("spawn").executes(ShileCommands::spawn))
				.then(Commands.literal("despawn").executes(ShileCommands::despawn))
				.then(Commands.literal("reset").executes(ShileCommands::reset))
				.then(Commands.literal("status").executes(ShileCommands::status))
				.then(Commands.literal("help").executes(ShileCommands::help))
			));
	}

	private static int spawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ShilePlayerState state = ShileManager.get(player.getUUID());
		if (state.isShileActive()) {
			return reply(context, "Shile zaten aktif. Durum: " + state.state().label() + ".");
		}
		boolean skippedCooldown = state.state() == ShileState.COOLDOWN;
		state.startEvent();

		// Oyuncunun tam arkası, 2 blok uzak (plan 5). Client kesin açıyı kendi bakışından alır.
		Direction facing = player.getDirection();
		ServerPlayNetworking.send(player, new ShileSpawnedPacket(
			player.getUUID(), player.blockPosition().relative(facing.getOpposite(), 2), facing));

		return reply(context, "Shile etkinleştirildi"
			+ (skippedCooldown ? " (cooldown atlandı)" : "")
			+ ". Durum: " + state.state().label() + ".");
	}

	private static int despawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ShilePlayerState state = ShileManager.get(player.getUUID());
		if (!state.isShileActive()) {
			return reply(context, "Aktif bir Shile bulunmuyor. Durum: " + state.state().label() + ".");
		}
		state.endActiveShile();
		ServerPlayNetworking.send(player, new MiniGameEndPacket(false));
		return reply(context, "Shile kaldırıldı (cooldown başlatılmadı). Durum: " + state.state().label() + ".");
	}

	private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ShilePlayerState state = ShileManager.get(player.getUUID());
		state.reset();
		ServerPlayNetworking.send(player, new MiniGameEndPacket(false));
		return reply(context, "Shile durumun sıfırlandı. Durum: " + state.state().label() + ".");
	}

	private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ShilePlayerState state = ShileManager.get(player.getUUID());
		return reply(context, "Shile durumu: " + describe(state) + ".");
	}

	private static String describe(ShilePlayerState state) {
		return switch (state.state()) {
			case ACTIVE -> "Aktif (kalan olay süresi: " + ticksToText(state.eventTicksRemaining()) + ")";
			case COOLDOWN -> "Cooldown (kalan: " + ticksToText(state.cooldownTicksRemaining()) + ")";
			default -> state.state().label();
		};
	}

	private static String ticksToText(int ticks) {
		int seconds = (ticks + 19) / 20;
		return seconds >= 60 ? (seconds / 60) + " dk " + (seconds % 60) + " sn" : seconds + " sn";
	}

	private static int help(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return reply(context, String.join("\n",
			"Shile komutları:",
			"/shile spawn - Shile olayını başlatır (test, cooldown'u atlar)",
			"/shile despawn - Süren Shile'ı kaldırır (cooldown başlatmaz)",
			"/shile reset - Olay ve cooldown dahil her şeyi sıfırlar",
			"/shile status - Durumu gösterir",
			"/shile help - Bu liste"));
	}

	/**
	 * Komutlar oyuncuya özeldir: konsol/komut bloğu gibi oyuncu olmayan kaynaklar reddedilir
	 * (getPlayerOrException). Durum ve paketler her zaman komutu çalıştıran oyuncuya gider.
	 */
	private static int reply(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		source.getPlayerOrException();
		source.sendSuccess(() -> Component.literal(message), false);
		return Command.SINGLE_SUCCESS;
	}
}

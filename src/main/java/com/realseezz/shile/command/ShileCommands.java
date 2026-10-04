package com.realseezz.shile.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.realseezz.shile.common.ShileState;
import com.realseezz.shile.server.ShilePlayerState;
import com.realseezz.shile.server.ShileServerManager;

/**
 * /shile komut ağacı. Komutlar artık server tarafındaki gerçek Shile durumunu değiştirir,
 * ama henüz görsel, ses, hareket ve sayaç yoktur.
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
		ShilePlayerState state = stateOf(context);
		if (state.isShileActive()) {
			return reply(context, "Shile zaten aktif. Durum: " + state.state().label() + ".");
		}
		boolean skippedCooldown = state.state() == ShileState.COOLDOWN;
		state.startEvent();
		return reply(context, "Shile etkinleştirildi"
			+ (skippedCooldown ? " (cooldown atlandı)" : "")
			+ ". Durum: " + state.state().label() + ". Görsel ve hareket henüz yok.");
	}

	private static int despawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ShilePlayerState state = stateOf(context);
		if (!state.isShileActive()) {
			return reply(context, "Aktif bir Shile bulunmuyor. Durum: " + state.state().label() + ".");
		}
		state.endActiveShile();
		return reply(context, "Shile kaldırıldı (cooldown başlatılmadı). Durum: " + state.state().label() + ".");
	}

	private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ShilePlayerState state = stateOf(context);
		state.reset();
		return reply(context, "Shile durumun sıfırlandı. Durum: " + state.state().label() + ".");
	}

	private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ShilePlayerState state = stateOf(context);
		return reply(context, "Shile durumu: " + state.state().label() + ".");
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
	 * Komutlar oyuncuya özeldir: konsol/komut bloğu gibi oyuncu olmayan kaynaklar reddedilir.
	 * Durum her zaman komutu çalıştıran oyuncuya aittir; hedef oyuncu argümanı yoktur.
	 */
	private static ShilePlayerState stateOf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		return ShileServerManager.get(player.getUUID());
	}

	private static int reply(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		source.getPlayerOrException();
		source.sendSuccess(() -> Component.literal(message), false);
		return Command.SINGLE_SUCCESS;
	}
}

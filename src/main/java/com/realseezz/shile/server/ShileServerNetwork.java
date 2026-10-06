package com.realseezz.shile.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.realseezz.shile.common.ShileState;
import com.realseezz.shile.common.network.MiniGameEndPacket;
import com.realseezz.shile.common.network.MiniGameFailedPacket;
import com.realseezz.shile.common.network.MiniGameStartedPacket;
import com.realseezz.shile.common.network.PlayerLookedAtPacket;

/**
 * Client -> Server paket alıcıları.
 * Mini oyun girdisi ve başarısızlık işleme ilgili adımlarda eklenecek.
 */
public final class ShileServerNetwork {
	private ShileServerNetwork() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(PlayerLookedAtPacket.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			ShilePlayerState state = ShileManager.get(player.getUUID());
			float duration = payload.lookDuration();

			// Yalnızca aktif Shile varken ve yeterli süre bildirildiyse (NaN da elenir).
			if (state.state() != ShileState.ACTIVE || !(duration >= 0.95f)) {
				return;
			}
			state.startMiniGame();
			// Tuşlar ilgili adımda eklenecek; şimdilik boş liste ile yalnızca "başladı" bilgisi gider.
			ServerPlayNetworking.send(player, new MiniGameStartedPacket(""));
			// GEÇİCİ: mini oyun henüz yok.
			player.sendSystemMessage(Component.literal(
				"Bakış algılandı: mini oyun başladı. Durum: " + state.state().label() + "."));
		});

		// Client mini oyunda başarısız oldu (süre doldu ya da yanlış tuş).
		ServerPlayNetworking.registerGlobalReceiver(MiniGameFailedPacket.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			ShilePlayerState state = ShileManager.get(player.getUUID());
			if (state.state() != ShileState.MINI_GAME) {
				return;
			}
			// Plan 12: başarısızlıkta yeni rastgele cooldown başlar (ölüm ekranında sayaç durur), oyuncu ani ölür.
			state.startCooldown();
			ServerPlayNetworking.send(player, new MiniGameEndPacket(false));
			player.kill((ServerLevel) player.level());
		});
	}
}

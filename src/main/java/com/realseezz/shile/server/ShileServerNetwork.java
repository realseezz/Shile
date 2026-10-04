package com.realseezz.shile.server;

import net.minecraft.network.chat.Component;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.realseezz.shile.common.network.PlayerLookedAtPacket;

/**
 * Client -> Server paket alıcıları. Şimdilik yalnızca ağ testi cevabı var;
 * gerçek bakış, mini oyun girdisi ve başarısızlık işleme sonraki adımlarda eklenecek.
 */
public final class ShileServerNetwork {
	private ShileServerNetwork() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(PlayerLookedAtPacket.TYPE, (payload, context) -> {
			if (payload.lookDuration() == PlayerLookedAtPacket.TEST_ECHO) {
				context.player().sendSystemMessage(
					Component.literal("Ağ testi başarılı: client paketi aldı ve server'a cevap verdi."));
			}
		});
	}
}

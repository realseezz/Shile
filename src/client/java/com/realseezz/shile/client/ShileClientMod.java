package com.realseezz.shile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.realseezz.shile.ShileMod;
import com.realseezz.shile.common.network.PlayerLookedAtPacket;
import com.realseezz.shile.common.network.ShileSpawnedPacket;

/**
 * Client tarafı başlatıcı. Şimdilik yalnızca ağ testi cevabı var;
 * render, bakış ve mini oyun sonraki adımlarda eklenecek.
 */
public class ShileClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ShileSpawnedPacket.TYPE, (payload, context) -> {
			ShileMod.LOGGER.info("ShileSpawnedPacket alindi: konum={}, yon={}", payload.position(), payload.direction());
			// GEÇİCİ ağ testi cevabı: server'a geri bildir.
			ClientPlayNetworking.send(new PlayerLookedAtPacket(PlayerLookedAtPacket.TEST_ECHO));
		});
	}
}

package com.realseezz.shile.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.realseezz.shile.client.renderer.ShileRenderer;
import com.realseezz.shile.common.network.MiniGameEndPacket;
import com.realseezz.shile.common.network.ShileSpawnedPacket;

/**
 * Client tarafı başlatıcı.
 */
public class ShileClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ShileRenderer.init();

		ClientPlayNetworking.registerGlobalReceiver(ShileSpawnedPacket.TYPE, (payload, context) -> {
			LocalPlayer player = context.client().player;
			if (player == null) {
				return;
			}
			// Paketteki konum/yön blok hassasiyetinde; kesin açı için oyuncunun kendi bakışı kullanılır.
			ShileClientState.spawnBehind(player.getYRot());
		});

		// Şimdilik olay bittiğinde (başarılı ya da başarısız) görseli temizler.
		ClientPlayNetworking.registerGlobalReceiver(MiniGameEndPacket.TYPE,
			(payload, context) -> ShileClientState.clear());

		// Shile'ın hareketi: her client level tick'inde (oyun duraklatılınca tick durur).
		ClientTickEvents.END_LEVEL_TICK.register(level -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				ShileClientState.tick(player.getYRot());
			}
		});

		// Dünyadan çıkınca görsel durum bir sonraki dünyaya sızmasın.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ShileClientState.clear());
	}
}

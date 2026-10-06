package com.realseezz.shile.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.realseezz.shile.client.hud.ShileHudOverlay;
import com.realseezz.shile.client.renderer.ShileRenderer;
import com.realseezz.shile.common.network.MiniGameEndPacket;
import com.realseezz.shile.common.network.MiniGameStartedPacket;
import com.realseezz.shile.common.network.ShileSpawnedPacket;

/**
 * Client tarafı başlatıcı.
 */
public class ShileClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ShileRenderer.init();
		ShileHudOverlay.register();

		ClientPlayNetworking.registerGlobalReceiver(ShileSpawnedPacket.TYPE, (payload, context) -> {
			LocalPlayer player = context.client().player;
			if (player == null) {
				return;
			}
			// Paketteki konum/yön blok hassasiyetinde; kesin açı için oyuncunun kendi bakışı kullanılır.
			ShileClientState.spawnBehind(player.getYRot());
		});

		// Mini oyun başlarken açık tüm ekranlar (envanter, sandık, chat vb.) kapatılır (plan 11).
		ClientPlayNetworking.registerGlobalReceiver(MiniGameStartedPacket.TYPE, (payload, context) -> {
			Minecraft client = context.client();
			MiniGameState.start(payload.keys());
			// 26.2'de ekranlar Minecraft yerine Gui üzerinden yönetilir (gui.screen(), gui.setScreen()).
			if (client.gui.screen() != null) {
				if (client.player != null) {
					client.player.closeContainer(); // açık sandık/envanter için server'a da kapatma bildirir
				}
				client.gui.setScreen(null);
			}
		});

		// Şimdilik olay bittiğinde (başarılı ya da başarısız) görseli temizler.
		ClientPlayNetworking.registerGlobalReceiver(MiniGameEndPacket.TYPE,
			(payload, context) -> ShileClientState.clear());

		// Shile'ın hareketi: her client level tick'inde (oyun duraklatılınca tick durur).
		ClientTickEvents.END_LEVEL_TICK.register(level -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				ShileClientState.tick(player.getYRot());
				GazeDetector.tick(player);
			}
		});

		// Dünyadan çıkınca görsel durum bir sonraki dünyaya sızmasın.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ShileClientState.clear());
	}
}

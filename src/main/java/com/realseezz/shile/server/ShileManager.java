package com.realseezz.shile.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.realseezz.shile.common.network.MiniGameEndPacket;

/**
 * Oyuncu UUID'sine göre Shile durumlarını tutar ve her server tick'inde sayaçları işletir.
 * Yalnızca server thread'inden kullanılır.
 *
 * Sayaçlar yalnızca oyuncu çevrimiçi ve hayattayken işler (ölüm ekranında ve çevrimdışıyken durur).
 * Durumlar şimdilik yalnızca bellekte durur; server durunca silinir. Cooldown'un dünya yeniden
 * açılışında kaldığı yerden devam etmesi için kalıcı kayıt cooldown adımında eklenecek.
 */
public final class ShileManager {
	private static final Map<UUID, ShilePlayerState> STATES = new HashMap<>();

	private ShileManager() {
	}

	public static void register() {
		// Tek oyunculuda dünyadan çıkıp başka dünyaya girince eski dünyanın durumu sızmasın.
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> STATES.clear());
		ServerTickEvents.END_SERVER_TICK.register(ShileManager::tick);
	}

	public static ShilePlayerState get(UUID playerId) {
		return STATES.computeIfAbsent(playerId, id -> new ShilePlayerState());
	}

	private static void tick(MinecraftServer server) {
		for (Map.Entry<UUID, ShilePlayerState> entry : STATES.entrySet()) {
			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			if (player == null || !player.isAlive()) {
				continue;
			}
			if (entry.getValue().tick() == ShilePlayerState.TickResult.EVENT_EXPIRED) {
				// Shile kayboldu: client görseli temizlesin.
				ServerPlayNetworking.send(player, new MiniGameEndPacket(false));
			}
		}
	}
}

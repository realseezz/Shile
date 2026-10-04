package com.realseezz.shile.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Oyuncu UUID'sine göre Shile durumlarını tutar. Yalnızca server thread'inden kullanılır.
 *
 * Durumlar şimdilik yalnızca bellekte durur; server durunca silinir.
 * Cooldown'un çıkış/giriş ve dünya yeniden açılışında korunması (plan 13 ve
 * Antigravity notları) cooldown adımında kalıcı kayıtla eklenecek.
 */
public final class ShileServerManager {
	private static final Map<UUID, ShilePlayerState> STATES = new HashMap<>();

	private ShileServerManager() {
	}

	public static void register() {
		// Tek oyunculuda dünyadan çıkıp başka dünyaya girince eski dünyanın durumu sızmasın.
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> STATES.clear());
	}

	public static ShilePlayerState get(UUID playerId) {
		return STATES.computeIfAbsent(playerId, id -> new ShilePlayerState());
	}
}

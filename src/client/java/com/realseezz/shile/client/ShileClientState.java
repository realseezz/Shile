package com.realseezz.shile.client;

/**
 * Client tarafındaki Shile görsel durumu. Yalnızca client ana thread'inden kullanılır.
 */
public final class ShileClientState {
	private static boolean active;
	/** Shile'ın oyuncuya göre bulunduğu yön, Minecraft yaw derecesi cinsinden. */
	private static float orbitYawDeg;

	private ShileClientState() {
	}

	/** Oyuncunun tam arkasında (180 derece) başlat. */
	public static void spawnBehind(float playerYawDeg) {
		active = true;
		orbitYawDeg = playerYawDeg + 180.0f;
	}

	public static void clear() {
		active = false;
	}

	public static boolean isActive() {
		return active;
	}

	public static float orbitYawDeg() {
		return orbitYawDeg;
	}
}

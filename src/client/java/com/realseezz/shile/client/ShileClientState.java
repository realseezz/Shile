package com.realseezz.shile.client;

import net.minecraft.util.Mth;

import com.realseezz.shile.common.ShileConstants;

/**
 * Client tarafındaki Shile görsel durumu ve hareketi. Yalnızca client ana thread'inden kullanılır.
 *
 * Hareket tick tabanlıdır (20 tick/sn): her tick açı 60/20 = 3 derece ilerler. Çizim, iki tick
 * arasını yumuşatmak için son tick'ten beri geçen süreye göre ara değer hesaplar.
 */
public final class ShileClientState {
	private static boolean active;
	/** Shile'ın yönü, Minecraft yaw derecesi (oyuncunun baktığı yön = oyuncunun önü). */
	private static float orbitYawDeg;
	private static float prevOrbitYawDeg;
	private static long lastTickNanos;

	private ShileClientState() {
	}

	/** Oyuncunun tam arkasında (180 derece) başlat. */
	public static void spawnBehind(float playerYawDeg) {
		active = true;
		orbitYawDeg = Mth.wrapDegrees(playerYawDeg + 180.0f);
		prevOrbitYawDeg = orbitYawDeg;
		lastTickNanos = System.nanoTime();
	}

	public static void clear() {
		active = false;
	}

	public static boolean isActive() {
		return active;
	}

	/**
	 * Her client level tick'inde çağrılır. Hedef oyuncunun önüdür (oyuncunun baktığı yön);
	 * Shile açısal olarak kısa yönden 60 derece/sn ile ilerler, ulaşınca durur.
	 * Oyuncu dönerse hedef her tick yeniden hesaplanır.
	 */
	public static void tick(float playerYawDeg) {
		if (!active) {
			return;
		}
		prevOrbitYawDeg = orbitYawDeg;

		float delta = Mth.wrapDegrees(playerYawDeg - orbitYawDeg);
		float step = (float) (ShileConstants.ROTATION_SPEED_DEG_PER_SECOND / ShileConstants.TICKS_PER_SECOND);

		if (Math.abs(delta) <= step) {
			orbitYawDeg = Mth.wrapDegrees(playerYawDeg);
		} else {
			orbitYawDeg = Mth.wrapDegrees(orbitYawDeg + Math.signum(delta) * step);
		}
		lastTickNanos = System.nanoTime();
	}

	/** Çizim için yumuşatılmış açı. Oyun duraklatılınca tick durur, açı son değerde kalır. */
	public static float renderYawDeg() {
		double partial = (System.nanoTime() - lastTickNanos) / 50_000_000.0;
		partial = Math.min(1.0, Math.max(0.0, partial));
		return prevOrbitYawDeg + Mth.wrapDegrees(orbitYawDeg - prevOrbitYawDeg) * (float) partial;
	}
}

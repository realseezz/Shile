package com.realseezz.shile.common;

import net.minecraft.resources.Identifier;

/**
 * Plan sabitleri (Antigravity bölümü ve plan 8, 9, 13). Değerler kod içinde dağılmasın diye burada toplanır.
 */
public final class ShileConstants {
	public static final String MOD_ID = "shile";

	public static final int TICKS_PER_SECOND = 20;

	// --- Hareket ---
	/** Oyuncunun çevresindeki yörünge yarıçapı (blok). */
	public static final double ORBIT_RADIUS_BLOCKS = 2.0;
	/** 6 saniyede 360 derece. */
	public static final double ROTATION_SPEED_DEG_PER_SECOND = 60.0;

	// --- Görünüm (blok cinsinden; PNG boyutundan bağımsız) ---
	/** Shile'ın çizilen genişliği. */
	public static final double SHILE_WIDTH_BLOCKS = 1.0;
	/** Shile'ın çizilen yüksekliği. */
	public static final double SHILE_HEIGHT_BLOCKS = 2.0;

	// --- Olay süresi ve bakış ---
	/** Shile çıktıktan sonra 30 saniyelik olay süresi. */
	public static final int EVENT_DURATION_TICKS = 30 * TICKS_PER_SECOND;
	/** Mini oyunu başlatan kesintisiz bakış süresi (1 saniye). */
	public static final int LOOK_DURATION_TICKS = 1 * TICKS_PER_SECOND;
	/** Görüş alanı hesabında varsayılan ekran oranı (16:9). FOV ayarı oyundan okunur. */
	public static final double ASSUMED_ASPECT_RATIO = 16.0 / 9.0;

	// --- Mini oyun ---
	public static final int MINI_GAME_MIN_VISIBLE_KEYS = 2;
	public static final int MINI_GAME_MAX_VISIBLE_KEYS = 3;
	/** Her tuş için süre (2 saniye; kullanıcı kararıyla Antigravity'deki 1 saniyeden yükseltildi). */
	public static final int KEY_DURATION_TICKS = 2 * TICKS_PER_SECOND;
	/** Yeni bir tuşun eklenme aralığı (0,5 saniye). */
	public static final int KEY_GAP_TICKS = TICKS_PER_SECOND / 2;
	/** Mini oyunda dayanılması gereken süre (15 saniye). */
	public static final int MINI_GAME_DURATION_TICKS = 15 * TICKS_PER_SECOND;
	/** Tuşlar ekran kenarından en az bu kadar slot uzakta çıkar. */
	public static final int KEY_EDGE_MARGIN_SLOTS = 2;
	/** Kullanılan tuşlar: A-Z. */
	public static final String KEY_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// --- Cooldown ---
	public static final int COOLDOWN_MIN_TICKS = 8 * 60 * TICKS_PER_SECOND;
	public static final int COOLDOWN_MAX_TICKS = 25 * 60 * TICKS_PER_SECOND;

	private ShileConstants() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}

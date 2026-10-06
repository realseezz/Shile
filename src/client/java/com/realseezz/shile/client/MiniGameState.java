package com.realseezz.shile.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.realseezz.shile.common.ShileConstants;

/**
 * Client tarafındaki mini oyun durumu: ekranda görünen tuşlar. Yalnızca client ana thread'inden kullanılır.
 *
 * Şimdilik yalnızca yer tutucu: mini oyun başlayınca rastgele harfler sabit konumlarda gösterilir.
 * Tuşların sırayla çıkıp kaybolması, süreler ve girdi sonraki adımlarda eklenecek.
 */
public final class MiniGameState {
	/**
	 * Ekrandaki bir tuş. Konum, ekran boyutundan bağımsız olması için 0-1 arası oran olarak saklanır;
	 * çizimde kenar boşluğu kurallarına göre bir slota çevrilir.
	 */
	public record VisibleKey(char letter, double xFraction, double yFraction) {
	}

	private static final List<VisibleKey> KEYS = new ArrayList<>();
	private static boolean active;

	private MiniGameState() {
	}

	/** Mini oyun başladı. Sunucudan harf gelmezse (şimdilik hep boş) rastgele farklı harfler seçilir. */
	public static void start(String letters) {
		KEYS.clear();
		active = true;

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int count = random.nextInt(ShileConstants.MINI_GAME_MIN_VISIBLE_KEYS, ShileConstants.MINI_GAME_MAX_VISIBLE_KEYS + 1);
		StringBuilder used = new StringBuilder();
		for (int i = 0; i < count; i++) {
			char letter;
			if (i < letters.length()) {
				letter = letters.charAt(i);
			} else {
				do {
					letter = ShileConstants.KEY_POOL.charAt(random.nextInt(ShileConstants.KEY_POOL.length()));
				} while (used.indexOf(String.valueOf(letter)) >= 0);
			}
			used.append(letter);
			KEYS.add(new VisibleKey(letter, random.nextDouble(), random.nextDouble()));
		}
	}

	public static void stop() {
		KEYS.clear();
		active = false;
	}

	public static boolean isActive() {
		return active;
	}

	public static List<VisibleKey> keys() {
		return KEYS;
	}
}

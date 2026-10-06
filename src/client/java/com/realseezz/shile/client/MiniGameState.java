package com.realseezz.shile.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.realseezz.shile.common.ShileConstants;
import com.realseezz.shile.common.network.MiniGameFailedPacket;

/**
 * Client tarafındaki mini oyun (plan 10): oyun tek tuşla başlar, her 0,5 saniyede bir yeni tuş eklenir;
 * aynı anda en fazla 2-3 tuş görünür. Her tuşun 2 saniyelik süresi vardır. Doğru tuşa basılınca tuş
 * kaybolur, yerine sonraki 0,5 saniyelik aralıkta (öncekinden farklı) yeni tuş gelir.
 * Süresi dolan ya da yanlış (görünmeyen bir harf) tuş başarısızlıktır; client server'a
 * {@link MiniGameFailedPacket} gönderir. 15 saniye dayanılırsa sonucu server kendi sayacıyla bildirir.
 *
 * Yalnızca client ana thread'inden kullanılır.
 */
public final class MiniGameState {
	/** Ekrandaki bir tuş. Konum, ekran boyutundan bağımsız olması için 0-1 arası oran olarak saklanır. */
	public static final class VisibleKey {
		private final char letter;
		private final double xFraction;
		private final double yFraction;
		private int ticksRemaining = ShileConstants.KEY_DURATION_TICKS;

		private VisibleKey(char letter, double xFraction, double yFraction) {
			this.letter = letter;
			this.xFraction = xFraction;
			this.yFraction = yFraction;
		}

		public char letter() {
			return letter;
		}

		public double xFraction() {
			return xFraction;
		}

		public double yFraction() {
			return yFraction;
		}

		public int ticksRemaining() {
			return ticksRemaining;
		}
	}

	private static final List<VisibleKey> KEYS = new ArrayList<>();
	private static final boolean[] WAS_DOWN = new boolean[ShileConstants.KEY_POOL.length()];

	private static boolean active;
	/** İlk yoklamada yalnızca tuş durumları okunur; mini oyun başlarken basılı tutulan tuş sayılmaz. */
	private static boolean baselineSet;
	/** 15 saniye doldu; sonucu server bildirecek. */
	private static boolean finishing;
	private static int elapsedTicks;
	private static char lastLetter;
	/** Bir sonraki tuşun eklenmesine kalan tick (her KEY_GAP_TICKS'te bir). */
	private static int spawnCountdown;
	/** Aynı anda en fazla kaç tuş görünür (oyun başında 2 ya da 3 seçilir). */
	private static int maxVisible;

	private MiniGameState() {
	}

	/** Mini oyun başladı. Server'dan harf gelmezse (şimdilik hep boş) rastgele farklı harfler seçilir. */
	public static void start(String letters) {
		KEYS.clear();
		active = true;
		baselineSet = false;
		finishing = false;
		elapsedTicks = 0;
		lastLetter = 0;

		ThreadLocalRandom random = ThreadLocalRandom.current();
		maxVisible = random.nextInt(ShileConstants.MINI_GAME_MIN_VISIBLE_KEYS, ShileConstants.MINI_GAME_MAX_VISIBLE_KEYS + 1);
		spawnCountdown = ShileConstants.KEY_GAP_TICKS;
		// Oyun tek tuşla başlar; yenileri her 0,5 saniyede bir eklenir.
		KEYS.add(newKey(letters.isEmpty() ? pickLetter() : letters.charAt(0)));
	}

	public static void stop() {
		KEYS.clear();
		active = false;
		finishing = false;
	}

	public static boolean isActive() {
		return active;
	}

	public static List<VisibleKey> keys() {
		return KEYS;
	}

	/** Her client level tick'inde çağrılır: tuş süreleri, bekleyen yeni tuşlar ve toplam süre. */
	public static void tick() {
		if (!active) {
			return;
		}
		elapsedTicks++;
		if (elapsedTicks >= ShileConstants.MINI_GAME_DURATION_TICKS) {
			// 15 saniye doldu: başarısızlık artık sayılmaz, sonucu server bildirir.
			finishing = true;
			KEYS.clear();
			return;
		}
		if (finishing) {
			return;
		}

		for (VisibleKey key : KEYS) {
			key.ticksRemaining--;
			if (key.ticksRemaining <= 0) {
				fail();
				return;
			}
		}

		spawnCountdown--;
		if (spawnCountdown <= 0) {
			spawnCountdown = ShileConstants.KEY_GAP_TICKS;
			if (KEYS.size() < maxVisible) {
				KEYS.add(newKey(pickLetter()));
			}
		}
	}

	/**
	 * Her karede çağrılır: A-Z tuşlarının yeni basıldığı anı yakalar. Tick yerine kare başına yoklanır,
	 * böylece çok kısa basışlar kaçmaz. Mini oyun sırasında tuş eşlemeleri engellendiği için ham tuş
	 * durumu doğrudan pencereden okunur. (26.2'de GLFW anahtar kodu = büyük harfin kodu.)
	 */
	public static void pollInput(Minecraft client) {
		if (!active) {
			return;
		}
		boolean firstPoll = !baselineSet;
		baselineSet = true;

		for (int i = 0; i < ShileConstants.KEY_POOL.length(); i++) {
			char letter = ShileConstants.KEY_POOL.charAt(i);
			boolean down = InputConstants.isKeyDown(client.getWindow(), letter);
			boolean pressed = down && !WAS_DOWN[i];
			WAS_DOWN[i] = down;
			if (firstPoll || finishing || !pressed) {
				continue;
			}

			VisibleKey hit = find(letter);
			if (hit != null) {
				KEYS.remove(hit);
				lastLetter = letter;
			} else {
				fail(); // yanlış tuş
				return;
			}
		}
	}

	private static void fail() {
		active = false;
		KEYS.clear();
		ClientPlayNetworking.send(new MiniGameFailedPacket());
	}

	private static VisibleKey find(char letter) {
		for (VisibleKey key : KEYS) {
			if (key.letter == letter) {
				return key;
			}
		}
		return null;
	}

	/** Şu an görünmeyen ve en son tamamlanan tuştan farklı bir harf seçer (aynı tuş peş peşe gelmez). */
	private static char pickLetter() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		char letter;
		do {
			letter = ShileConstants.KEY_POOL.charAt(random.nextInt(ShileConstants.KEY_POOL.length()));
		} while (letter == lastLetter || find(letter) != null);
		return letter;
	}

	private static VisibleKey newKey(char letter) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new VisibleKey(letter, random.nextDouble(), random.nextDouble());
	}
}

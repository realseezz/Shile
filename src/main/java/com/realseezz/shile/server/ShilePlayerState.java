package com.realseezz.shile.server;

import java.util.concurrent.ThreadLocalRandom;

import com.realseezz.shile.common.ShileConstants;
import com.realseezz.shile.common.ShileState;

/**
 * Tek bir oyuncunun server tarafındaki Shile durumu.
 */
public final class ShilePlayerState {
	/** Bir server tick'inin sonucu. */
	public enum TickResult {
		NONE,
		/** 30 saniyelik olay süresi doldu, Shile kayboldu, cooldown başladı. */
		EVENT_EXPIRED,
		/** Mini oyunun 15 saniyesi hatasız doldu: başarı, Shile kayboldu, cooldown başladı. */
		MINI_GAME_SUCCEEDED,
		/** Cooldown bitti. */
		COOLDOWN_FINISHED
	}

	private ShileState state = ShileState.IDLE;

	/** Yalnızca ACTIVE iken anlamlı. */
	private int eventTicksRemaining;

	/** Yalnızca MINI_GAME iken anlamlı. */
	private int miniGameTicksRemaining;

	/** Yalnızca COOLDOWN iken anlamlı. */
	private int cooldownTicksRemaining;

	public ShileState state() {
		return state;
	}

	public int eventTicksRemaining() {
		return eventTicksRemaining;
	}

	public int miniGameTicksRemaining() {
		return miniGameTicksRemaining;
	}

	public int cooldownTicksRemaining() {
		return cooldownTicksRemaining;
	}

	/** ACTIVE veya MINI_GAME: bir Shile olayı sürüyor. */
	public boolean isShileActive() {
		return state == ShileState.ACTIVE || state == ShileState.MINI_GAME;
	}

	/** Yeni bir Shile olayı başlatır; varsa cooldown'u iptal eder. */
	public void startEvent() {
		state = ShileState.ACTIVE;
		eventTicksRemaining = ShileConstants.EVENT_DURATION_TICKS;
		cooldownTicksRemaining = 0;
	}

	/** Boyut değişince 30 saniyelik olay süresi baştan başlar (yalnızca ACTIVE iken). Henüz bağlı değil. */
	public void restartEventTimer() {
		if (state == ShileState.ACTIVE) {
			eventTicksRemaining = ShileConstants.EVENT_DURATION_TICKS;
		}
	}

	/** Bakış algılandı: mini oyun başlar, 30 saniyelik olay süresi geçersiz olur (yalnızca ACTIVE iken). */
	public void startMiniGame() {
		if (state == ShileState.ACTIVE) {
			state = ShileState.MINI_GAME;
			eventTicksRemaining = 0;
			miniGameTicksRemaining = ShileConstants.MINI_GAME_DURATION_TICKS;
		}
	}

	/** Olayı bitirip 8-25 dakikalık rastgele cooldown başlatır. */
	public void startCooldown() {
		state = ShileState.COOLDOWN;
		eventTicksRemaining = 0;
		miniGameTicksRemaining = 0;
		cooldownTicksRemaining = ThreadLocalRandom.current()
			.nextInt(ShileConstants.COOLDOWN_MIN_TICKS, ShileConstants.COOLDOWN_MAX_TICKS + 1);
	}

	/** Her server tick'inde, oyuncu çevrimiçi ve hayattayken çağrılır. */
	public TickResult tick() {
		if (state == ShileState.ACTIVE) {
			eventTicksRemaining--;
			if (eventTicksRemaining <= 0) {
				startCooldown();
				return TickResult.EVENT_EXPIRED;
			}
		} else if (state == ShileState.MINI_GAME) {
			miniGameTicksRemaining--;
			if (miniGameTicksRemaining <= 0) {
				startCooldown();
				return TickResult.MINI_GAME_SUCCEEDED;
			}
		} else if (state == ShileState.COOLDOWN) {
			cooldownTicksRemaining--;
			if (cooldownTicksRemaining <= 0) {
				state = ShileState.IDLE;
				cooldownTicksRemaining = 0;
				return TickResult.COOLDOWN_FINISHED;
			}
		}
		return TickResult.NONE;
	}

	/** Süren Shile olayını cooldown başlatmadan kaldırır (test komutu). */
	public void endActiveShile() {
		if (!isShileActive()) {
			return;
		}
		state = ShileState.IDLE;
		eventTicksRemaining = 0;
		miniGameTicksRemaining = 0;
	}

	/** Her şeyi (olay ve cooldown dahil) sıfırlar. */
	public void reset() {
		state = ShileState.IDLE;
		eventTicksRemaining = 0;
		miniGameTicksRemaining = 0;
		cooldownTicksRemaining = 0;
	}
}

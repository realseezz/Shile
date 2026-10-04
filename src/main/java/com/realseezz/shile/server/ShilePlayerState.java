package com.realseezz.shile.server;

import com.realseezz.shile.common.ShileConstants;
import com.realseezz.shile.common.ShileState;

/**
 * Tek bir oyuncunun server tarafındaki Shile durumu.
 *
 * Sayaçlar (olay süresi, cooldown) şimdilik yalnızca alan olarak durur;
 * her tick azaltılmaları sonraki adımda bağlanacak.
 */
public final class ShilePlayerState {
	private ShileState state = ShileState.IDLE;

	/** Yalnızca ACTIVE iken anlamlı. */
	private int eventTicksRemaining;

	/** Yalnızca COOLDOWN iken anlamlı. Çıkış/girişte kaldığı yerden devam eder. */
	private int cooldownTicksRemaining;

	public ShileState state() {
		return state;
	}

	public int eventTicksRemaining() {
		return eventTicksRemaining;
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

	/** Boyut değişince 30 saniyelik olay süresi baştan başlar (yalnızca ACTIVE iken). */
	public void restartEventTimer() {
		if (state == ShileState.ACTIVE) {
			eventTicksRemaining = ShileConstants.EVENT_DURATION_TICKS;
		}
	}

	/** Süren Shile olayını cooldown başlatmadan kaldırır (test komutu). */
	public void endActiveShile() {
		if (!isShileActive()) {
			return;
		}
		state = ShileState.IDLE;
		eventTicksRemaining = 0;
	}

	/** Her şeyi (olay ve cooldown dahil) sıfırlar. */
	public void reset() {
		state = ShileState.IDLE;
		eventTicksRemaining = 0;
		cooldownTicksRemaining = 0;
	}
}

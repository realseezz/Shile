package com.realseezz.shile.common;

/**
 * Bir oyuncunun Shile durumu (plan 12, 13 ve 8'deki akıştan).
 */
public enum ShileState {
	/** Shile yok, cooldown da yok. */
	IDLE("Boşta"),
	/** Shile çıktı, 30 saniyelik olay süresi işliyor. */
	ACTIVE("Aktif"),
	/** Mini oyun başladı; 30 saniyelik olay süresi artık geçersiz (plan 8). */
	MINI_GAME("Mini oyun"),
	/** Shile bitti, 8-25 dakikalık cooldown işliyor. */
	COOLDOWN("Cooldown");

	private final String label;

	ShileState(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}

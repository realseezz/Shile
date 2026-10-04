package com.realseezz.shile;

public final class ShileConstants {
	public static final String MOD_ID = "shile";

	public static final int TICKS_PER_SECOND = 20;

	/** Plan 8: Shile ortaya çıktıktan sonra 30 saniyelik olay süresi. */
	public static final int EVENT_DURATION_TICKS = 30 * TICKS_PER_SECOND;

	/** Plan 13: cooldown 8 ile 25 dakika arasında rastgele. */
	public static final int COOLDOWN_MIN_TICKS = 8 * 60 * TICKS_PER_SECOND;
	public static final int COOLDOWN_MAX_TICKS = 25 * 60 * TICKS_PER_SECOND;

	private ShileConstants() {
	}
}

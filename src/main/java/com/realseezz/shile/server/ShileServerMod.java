package com.realseezz.shile.server;

import com.realseezz.shile.command.ShileCommands;

/**
 * Server tarafı başlatıcı. Mantık client'a ihtiyaç duymaz; ShileMod tarafından çağrılır.
 */
public final class ShileServerMod {
	private ShileServerMod() {
	}

	public static void init() {
		ShileManager.register();
		ShileServerNetwork.register();
		ShileCommands.register();
	}
}

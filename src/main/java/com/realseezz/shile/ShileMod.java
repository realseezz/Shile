package com.realseezz.shile;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realseezz.shile.command.ShileCommands;
import com.realseezz.shile.server.ShileServerManager;

public class ShileMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ShileConstants.MOD_ID);

	@Override
	public void onInitialize() {
		ShileServerManager.register();
		ShileCommands.register();
		LOGGER.info("Shile yuklendi (Phase 2: server durum temeli).");
	}
}

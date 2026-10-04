package com.realseezz.shile;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realseezz.shile.common.ShileConstants;
import com.realseezz.shile.common.network.ShilePackets;
import com.realseezz.shile.server.ShileServerMod;

public class ShileMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ShileConstants.MOD_ID);

	@Override
	public void onInitialize() {
		// Paket türleri receiver'lardan önce kaydedilmeli.
		ShilePackets.register();
		ShileServerMod.init();
		LOGGER.info("Shile yuklendi (ag temeli).");
	}
}

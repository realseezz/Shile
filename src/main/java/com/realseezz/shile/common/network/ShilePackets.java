package com.realseezz.shile.common.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Paket türlerini kaydeder. Hem client hem server tarafında, receiver'lardan önce çağrılmalıdır.
 */
public final class ShilePackets {
	private ShilePackets() {
	}

	public static void register() {
		// Server -> Client
		PayloadTypeRegistry.clientboundPlay().register(ShileSpawnedPacket.TYPE, ShileSpawnedPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(MiniGameStartedPacket.TYPE, MiniGameStartedPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(MiniGameEndPacket.TYPE, MiniGameEndPacket.CODEC);

		// Client -> Server
		PayloadTypeRegistry.serverboundPlay().register(PlayerLookedAtPacket.TYPE, PlayerLookedAtPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(MiniGameInputPacket.TYPE, MiniGameInputPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(MiniGameFailedPacket.TYPE, MiniGameFailedPacket.CODEC);
	}
}

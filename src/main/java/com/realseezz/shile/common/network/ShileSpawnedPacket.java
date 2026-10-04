package com.realseezz.shile.common.network;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/** Server -> Client: Shile ortaya çıktı. */
public record ShileSpawnedPacket(UUID playerId, BlockPos position, Direction direction) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ShileSpawnedPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("shile_spawned"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShileSpawnedPacket> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, ShileSpawnedPacket::playerId,
		BlockPos.STREAM_CODEC, ShileSpawnedPacket::position,
		Direction.STREAM_CODEC, ShileSpawnedPacket::direction,
		ShileSpawnedPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

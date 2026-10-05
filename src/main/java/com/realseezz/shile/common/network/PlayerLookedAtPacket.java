package com.realseezz.shile.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/** Client -> Server: oyuncu Shile'a bu kadar süre kesintisiz baktı. */
public record PlayerLookedAtPacket(float lookDuration) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PlayerLookedAtPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("player_looked_at"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLookedAtPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, PlayerLookedAtPacket::lookDuration,
		PlayerLookedAtPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

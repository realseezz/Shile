package com.realseezz.shile.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/** Server -> Client: mini oyun / Shile olayı bitti. */
public record MiniGameEndPacket(boolean success) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MiniGameEndPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("mini_game_end"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MiniGameEndPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, MiniGameEndPacket::success,
		MiniGameEndPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

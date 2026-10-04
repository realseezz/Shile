package com.realseezz.shile.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/** Client -> Server: mini oyun başarısız (süre doldu veya yanlış tuş). */
public record MiniGameFailedPacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MiniGameFailedPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("mini_game_failed"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MiniGameFailedPacket> CODEC =
		StreamCodec.unit(new MiniGameFailedPacket());

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

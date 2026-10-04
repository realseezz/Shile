package com.realseezz.shile.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/** Client -> Server: basılan tuş ve tepki süresi. */
public record MiniGameInputPacket(char keyPressed, long responseTime) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MiniGameInputPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("mini_game_input"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MiniGameInputPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, (MiniGameInputPacket packet) -> (int) packet.keyPressed(),
		ByteBufCodecs.LONG, (MiniGameInputPacket packet) -> packet.responseTime(),
		(Integer key, Long time) -> new MiniGameInputPacket((char) key.intValue(), time)
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

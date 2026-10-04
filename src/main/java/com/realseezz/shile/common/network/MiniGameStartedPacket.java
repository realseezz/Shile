package com.realseezz.shile.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.realseezz.shile.common.ShileConstants;

/**
 * Server -> Client: mini oyun başladı.
 * Planda char[] idi; record'larda dizi karşılaştırması sorunlu olduğu için A-Z harflerini
 * tutan String kullanılır (içerik aynı).
 */
public record MiniGameStartedPacket(String keys) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MiniGameStartedPacket> TYPE =
		new CustomPacketPayload.Type<>(ShileConstants.id("mini_game_started"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MiniGameStartedPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, MiniGameStartedPacket::keys,
		MiniGameStartedPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

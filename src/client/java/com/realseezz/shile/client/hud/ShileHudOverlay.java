package com.realseezz.shile.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import com.realseezz.shile.client.MiniGameState;
import com.realseezz.shile.common.ShileConstants;

/**
 * Mini oyun overlay'i: HUD katmanında, ekranı kaplamadan tuşları çizer (plan 11).
 * Tuşların görsel tasarımı henüz kullanıcı tarafından belirlenmedi; şimdiki görünüm geçici yer tutucudur.
 */
public final class ShileHudOverlay {
	/** Bir tuş slotunun kenar uzunluğu (GUI piksel). */
	private static final int SLOT_SIZE = 24;

	private ShileHudOverlay() {
	}

	public static void register() {
		// Sohbet katmanından hemen önce çizilir (Fabric 26.2 Hud API'si).
		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
			ShileConstants.id("mini_game_overlay"), ShileHudOverlay::extract);
	}

	private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (!MiniGameState.isActive()) {
			return;
		}
		Minecraft client = Minecraft.getInstance();
		int columns = client.getWindow().getGuiScaledWidth() / SLOT_SIZE;
		int rows = client.getWindow().getGuiScaledHeight() / SLOT_SIZE;

		// Tuşlar ekran kenarından en az KEY_EDGE_MARGIN_SLOTS slot uzakta çıkar.
		int margin = ShileConstants.KEY_EDGE_MARGIN_SLOTS;
		int usableColumns = columns - margin * 2;
		int usableRows = rows - margin * 2;
		if (usableColumns < 1 || usableRows < 1) {
			return;
		}

		for (MiniGameState.VisibleKey key : MiniGameState.keys()) {
			int column = margin + Math.min(usableColumns - 1, (int) (key.xFraction() * usableColumns));
			int row = margin + Math.min(usableRows - 1, (int) (key.yFraction() * usableRows));
			int x = column * SLOT_SIZE;
			int y = row * SLOT_SIZE;

			graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xCC202020);
			graphics.outline(x, y, SLOT_SIZE, SLOT_SIZE, 0xFFFFFFFF);
			// Harf yaklaşık ortalanır (yazı tipi genişliği yerine sabit tahmin, yer tutucu).
			graphics.text(client.font, String.valueOf(key.letter()),
				x + (SLOT_SIZE - 6) / 2, y + (SLOT_SIZE - 8) / 2, 0xFFFFFFFF, false);
		}
	}
}

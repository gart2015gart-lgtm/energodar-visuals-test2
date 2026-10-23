package com.energodar.visuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Меню настроек (открывается на Right Shift). */
public class ConfigScreen extends Screen {
	private static final int[] PALETTE = {
		0xFF3B3B, 0xFF9A1F, 0xFFE23B, 0x4DFF6A, 0x3BE8FF, 0x3B7BFF, 0x7C4DFF, 0xFF4DD2, 0xFFFFFF
	};

	public ConfigScreen() {
		super(Component.literal("Energodar Visuals"));
	}

	@Override
	protected void init() {
		Config c = Config.get();
		int colW = 126;
		int gap = 6;
		int row = 22;
		int x0 = (this.width - (colW * 2 + gap)) / 2;
		int y0 = 42;
		int[] cx = {x0, x0 + colW + gap};

		int r = 0;
		toggle(cx[0], y0 + row * r++, colW, "Свап элитры", "Elytra swap", () -> c.elytraSwap, v -> c.elytraSwap = v);
		toggle(cx[0], y0 + row * r++, colW, "Яркость", "Fullbright", () -> c.fullbright, v -> c.fullbright = v);
		toggle(cx[0], y0 + row * r++, colW, "Частицы удара", "Hit particles", () -> c.hitParticles, v -> c.hitParticles = v);
		toggle(cx[0], y0 + row * r++, colW, "Звук удара", "Hit sound", () -> c.hitSound, v -> c.hitSound = v);
		color(cx[0], y0 + row * r++, colW, "Акцент HUD", "HUD accent", () -> c.accent, v -> c.accent = v);

		r = 0;
		toggle(cx[1], y0 + row * r++, colW, "HUD", "HUD", () -> c.hud, v -> c.hud = v);
		toggle(cx[1], y0 + row * r++, colW, "Ватермарк", "Watermark", () -> c.hudWatermark, v -> c.hudWatermark = v);
		toggle(cx[1], y0 + row * r++, colW, "Координаты", "Coords", () -> c.hudCoords, v -> c.hudCoords = v);
		toggle(cx[1], y0 + row * r++, colW, "Броня", "Armor", () -> c.hudArmor, v -> c.hudArmor = v);
		toggle(cx[1], y0 + row * r++, colW, "Таргет HUD", "Target HUD", () -> c.hudTarget, v -> c.hudTarget = v);

		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
			.bounds(this.width / 2 - 75, y0 + row * 5 + 8, 150, 20).build());
	}

	private void toggle(int x, int y, int w, String ru, String en, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		String label = Text.tr(ru, en);
		addRenderableWidget(Button.builder(toggleText(label, getter.get()), b -> {
			boolean next = !getter.get();
			setter.accept(next);
			b.setMessage(toggleText(label, next));
		}).bounds(x, y, w, 20).build());
	}

	private static Component toggleText(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "§a" + Text.tr("ВКЛ", "ON") : "§c" + Text.tr("ВЫКЛ", "OFF")));
	}

	private void color(int x, int y, int w, String ru, String en, IntSupplier getter, IntConsumer setter) {
		String label = Text.tr(ru, en);
		addRenderableWidget(Button.builder(colorText(label, getter.getAsInt()), b -> {
			int cur = getter.getAsInt();
			int idx = 0;
			for (int i = 0; i < PALETTE.length; i++) {
				if (PALETTE[i] == cur) {
					idx = i + 1;
					break;
				}
			}
			int next = PALETTE[idx % PALETTE.length];
			setter.accept(next);
			b.setMessage(colorText(label, next));
		}).bounds(x, y, w, 20).build());
	}

	private static Component colorText(String label, int rgb) {
		return Component.literal(label + " ").append(Component.literal("■■").withColor(rgb));
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);

		int colW = 126;
		int gap = 6;
		int x0 = (this.width - (colW * 2 + gap)) / 2;
		g.drawCenteredString(this.font, Text.tr("Основное", "General"), x0 + colW / 2, 30, 0xFFAAAAAA);
		g.drawCenteredString(this.font, "HUD", x0 + colW + gap + colW / 2, 30, 0xFFAAAAAA);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void removed() {
		Config.save();
	}
}

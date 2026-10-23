package com.energodar.visuals;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/** HUD: ватермарк, координаты, прочность брони, Target HUD. */
public final class Hud {
	private Hud() {}

	private static final EquipmentSlot[] ARMOR = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private static LivingEntity target;
	private static long targetTime;

	public static void onAttack(Entity e) {
		if (e instanceof LivingEntity le) {
			target = le;
			targetTime = System.currentTimeMillis();
		}
	}

	public static void render(GuiGraphics g, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		Config c = Config.get();
		LocalPlayer p = mc.player;
		if (!c.hud || p == null || mc.options.hideGui) return;

		Font font = mc.font;
		int accent = 0xFF000000 | c.accent;
		int w = g.guiWidth();
		int h = g.guiHeight();

		int y = 5;

		if (c.hudWatermark) {
			String name = "Energodar Visuals";
			String info = mc.getFps() + " FPS  " + ping(mc, p) + " ms";
			int nameW = font.width(name);
			int total = nameW + 6 + font.width(info) + 10;
			g.fill(5, y, 5 + total, y + 14, 0x90000000);
			g.fill(5, y, 7, y + 14, accent);
			g.drawString(font, name, 11, y + 3, accent, true);
			g.drawString(font, info, 11 + nameW + 6, y + 3, 0xFFFFFFFF, true);
			y += 18;
		}

		if (c.hudCoords) {
			String s = "XYZ  " + p.getBlockX() + "  " + p.getBlockY() + "  " + p.getBlockZ();
			g.fill(5, y, 5 + font.width(s) + 10, y + 14, 0x90000000);
			g.fill(5, y, 7, y + 14, accent);
			g.drawString(font, s, 11, y + 3, 0xFFFFFFFF, true);
		}

		if (c.hudArmor) {
			int ay = h / 2 - 36;
			for (EquipmentSlot slot : ARMOR) {
				ItemStack st = p.getItemBySlot(slot);
				if (st.isEmpty()) continue;
				g.renderItem(st, 6, ay);
				if (st.isDamageableItem()) {
					int left = st.getMaxDamage() - st.getDamageValue();
					int pct = Mth.clamp(left * 100 / Math.max(1, st.getMaxDamage()), 0, 100);
					g.drawString(font, pct + "%", 26, ay + 4, 0xFF000000 | st.getBarColor(), true);
				}
				ay += 18;
			}
		}

		renderTarget(g, font, c, accent, w, h);
	}

	private static void renderTarget(GuiGraphics g, Font font, Config c, int accent, int w, int h) {
		LivingEntity t = target;
		if (!c.hudTarget || t == null) return;
		if (t.isRemoved() || !t.isAlive() || System.currentTimeMillis() - targetTime > 5000) {
			target = null;
			return;
		}

		int bx = w / 2 + 24;
		int by = h / 2 + 10;
		int bw = 112;
		int bh = 38;

		g.fill(bx, by, bx + bw, by + bh, 0x90000000);
		g.fill(bx, by, bx + 2, by + bh, accent);
		g.drawString(font, t.getName().getString(), bx + 7, by + 5, 0xFFFFFFFF, true);

		float hp = t.getHealth();
		float max = Math.max(1f, t.getMaxHealth());
		float ratio = Mth.clamp(hp / max, 0f, 1f);
		int barX = bx + 7;
		int barW = bw - 14;
		g.fill(barX, by + 19, barX + barW, by + 24, 0xFF303030);
		g.fill(barX, by + 19, barX + (int) (barW * ratio), by + 24, accent);
		g.drawString(font, String.format(Locale.ROOT, "%.1f / %.1f HP", hp, max), barX, by + 27, 0xFFDDDDDD, true);
	}

	private static int ping(Minecraft mc, LocalPlayer p) {
		ClientPacketListener conn = mc.getConnection();
		if (conn == null) return 0;
		PlayerInfo info = conn.getPlayerInfo(p.getUUID());
		return info == null ? 0 : info.getLatency();
	}
}

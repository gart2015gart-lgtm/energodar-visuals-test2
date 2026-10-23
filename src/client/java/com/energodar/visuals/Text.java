package com.energodar.visuals;

import net.minecraft.client.Minecraft;

/** Простая локализация: русский / английский. */
public final class Text {
	private Text() {}

	public static boolean ru() {
		try {
			String code = Minecraft.getInstance().options.languageCode;
			return code != null && (code.startsWith("ru") || code.startsWith("uk") || code.startsWith("be"));
		} catch (Throwable t) {
			return false;
		}
	}

	public static String tr(String ru, String en) {
		return ru() ? ru : en;
	}
}

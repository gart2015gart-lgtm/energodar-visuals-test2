package com.energodar.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Настройки мода. Хранятся в config/energodar_visuals.json */
public final class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("energodar_visuals.json");
	private static Config instance = new Config();

	// Elytra swap
	public boolean elytraSwap = true;

	// Яркость
	public boolean fullbright = false;

	// HUD
	public boolean hud = true;
	public boolean hudWatermark = true;
	public boolean hudCoords = true;
	public boolean hudArmor = true;
	public boolean hudTarget = true;
	public int accent = 0x7C4DFF;

	// Эффекты удара
	public boolean hitParticles = true;
	public boolean hitSound = false;

	public static Config get() {
		return instance;
	}

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				Config loaded = GSON.fromJson(Files.readString(FILE), Config.class);
				if (loaded != null) {
					instance = loaded;
				}
			}
		} catch (Exception e) {
			System.err.println("[Energodar Visuals] Не удалось прочитать конфиг: " + e);
		}
		save();
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(instance));
		} catch (IOException e) {
			System.err.println("[Energodar Visuals] Не удалось сохранить конфиг: " + e);
		}
	}
}

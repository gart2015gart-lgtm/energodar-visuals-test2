package com.energodar.visuals;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EnergodarVisuals implements ClientModInitializer {
	public static final String MOD_ID = "energodar_visuals";

	private static KeyMapping openGui;
	private static KeyMapping elytraSwap;
	private static boolean fullbrightApplied;

	@Override
	public void onInitializeClient() {
		Config.load();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.energodar_visuals.open_gui", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));
		elytraSwap = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.energodar_visuals.elytra_swap", InputConstants.Type.KEYSYM, InputConstants.KEY_V, category));

		ClientTickEvents.END_CLIENT_TICK.register(EnergodarVisuals::tick);

		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
			Hud::render);

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
			if (!(level instanceof ClientLevel)) return InteractionResult.PASS;
			Config c = Config.get();
			Hud.onAttack(entity);
			if (entity instanceof LivingEntity) {
				if (c.hitParticles) {
					player.crit(entity);
					player.magicCrit(entity);
					for (int i = 0; i < 6; i++) {
						level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
							entity.getRandomX(0.6), entity.getRandomY(), entity.getRandomZ(0.6), 0.0, 0.1, 0.0);
					}
				}
				if (c.hitSound) {
					level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(),
						SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8f, 1.2f, false);
				}
			}
			return InteractionResult.PASS;
		});
	}

	private static void tick(Minecraft mc) {
		while (openGui.consumeClick()) {
			if (mc.screen == null) mc.setScreen(new ConfigScreen());
		}
		while (elytraSwap.consumeClick()) {
			if (Config.get().elytraSwap) ElytraSwap.swap(mc);
		}
		fullbright(mc);
	}

	/** Яркость: клиентский эффект ночного зрения. */
	private static void fullbright(Minecraft mc) {
		LocalPlayer p = mc.player;
		if (p == null) {
			fullbrightApplied = false;
			return;
		}
		if (Config.get().fullbright) {
			MobEffectInstance cur = p.getEffect(MobEffects.NIGHT_VISION);
			if (cur == null || (fullbrightApplied && cur.getDuration() < 400)) {
				p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0, false, false, false));
				fullbrightApplied = true;
			}
		} else if (fullbrightApplied) {
			p.removeEffect(MobEffects.NIGHT_VISION);
			fullbrightApplied = false;
		}
	}
}

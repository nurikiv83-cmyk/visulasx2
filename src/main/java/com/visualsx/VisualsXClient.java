package com.visualsx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class VisualsXClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.load();

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
            Identifier.of("visualsx", "hud"), Hud::render);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, helper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                helper.register(new CosmeticsFeature(playerRenderer));
            }
        });

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("visualsx", "main"));
        KeyBinding menu = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.visualsx.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
        KeyBinding sync = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.visualsx.sync", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, cat));

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            while (menu.wasPressed()) {
                if (c.currentScreen == null) c.setScreen(new MenuScreen());
            }
            while (sync.wasPressed()) {
                if (c.player != null && FreeLook.active()) c.player.setYaw(FreeLook.yaw);
            }
            if (!FreeLook.active()) FreeLook.follow();
        });
    }
}

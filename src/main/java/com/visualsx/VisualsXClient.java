package com.visualsx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class VisualsXClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
            Identifier.of("visualsx", "hud"), Hud::render);

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("visualsx", "main"));
        KeyBinding toggle = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.visualsx.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
        KeyBinding sync = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.visualsx.sync", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, cat));

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            while (toggle.wasPressed()) {
                FreeLook.enabled = !FreeLook.enabled;
                if (c.player != null)
                    c.player.sendMessage(Text.literal("Erkin qarash: " + (FreeLook.enabled ? "ON" : "OFF")), true);
            }
            while (sync.wasPressed()) {
                if (c.player != null && FreeLook.active()) c.player.setYaw(FreeLook.yaw);
            }
            if (!FreeLook.active()) FreeLook.follow();
        });
    }
}

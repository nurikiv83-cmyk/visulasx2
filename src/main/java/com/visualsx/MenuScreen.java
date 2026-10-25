package com.visualsx;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** _VisualsX_ menyusi (Right Shift). Yuqorida bo'limlar, ostida yoqish/o'chirish tugmalari. */
public class MenuScreen extends Screen {
    private static final String[] TABS = {"HUD", "Kamera", "Kosmetika"};
    private static int tab = 0;

    public MenuScreen() {
        super(Text.literal("_VisualsX_"));
    }

    @Override
    protected void init() {
        int tw = Math.min(110, (width - 20) / TABS.length);
        int x0 = (width - tw * TABS.length) / 2;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            Text label = Text.literal(TABS[i]).formatted(i == tab ? Formatting.YELLOW : Formatting.WHITE);
            addDrawableChild(ButtonWidget.builder(label, b -> { tab = idx; clearAndInit(); })
                .dimensions(x0 + i * tw, 28, tw - 2, 20).build());
        }

        int cw = Math.min(190, width / 2 - 10);
        int left = width / 2 - cw - 2, right = width / 2 + 2;
        int y = 62;

        if (tab == 0) {
            toggle(left, y, cw, "Ping", () -> Config.ping, v -> Config.ping = v);
            toggle(right, y, cw, "FPS / CPS / TPS", () -> Config.stats, v -> Config.stats = v);
            toggle(left, y + 24, cw, "Logotip", () -> Config.logo, v -> Config.logo = v);
            toggle(right, y + 24, cw, "WASD tugmalari", () -> Config.keys, v -> Config.keys = v);
            toggle(left, y + 48, cw, "Potion effektlari", () -> Config.potions, v -> Config.potions = v);
            toggle(right, y + 48, cw, "Orqa inventar 9x3", () -> Config.inventory, v -> Config.inventory = v);
        } else if (tab == 1) {
            toggle(left, y, cw * 2 + 4, "Erkin kamera (F5)", () -> FreeLook.enabled, v -> FreeLook.enabled = v);
        }
    }

    private void toggle(int x, int y, int w, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(label(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(label(name, get.getAsBoolean()));
            Config.save();
        }).dimensions(x, y, w, 20).build());
    }

    private Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF")).formatted(on ? Formatting.GREEN : Formatting.RED);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFFFF);
        if (tab == 1) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("F5 da kamera alohida aylanadi, V - o'yinchini kamera tomonga buradi"),
                width / 2, 94, 0xFFAAAAAA);
        } else if (tab == 2) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Kosmetika (qanotlar, shlyapalar) keyingi yangilanishda qo'shiladi"),
                width / 2, 70, 0xFFAAAAAA);
        }
    }
}

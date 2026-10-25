package com.visualsx;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** _VisualsX_ menyusi (Right Shift): shaffof panel, HUD + Kamera + Kosmetika bitta faylda. */
public class MenuScreen extends Screen {

    /** Kosmetika tanlovi. Wings: 0-2, Hats: 3-5. Har guruhda bittasi yoqiladi. */
    public static class Cosmetics {
        public static final String[] NAMES = {
            "Shadow Wings", "Angel Wings", "Demon Wings", "Crown", "Top Hat", "Bunny Ears"};
        public static final boolean[] ON = new boolean[NAMES.length];
        private static boolean loaded;

        private static Path file() {
            return FabricLoader.getInstance().getConfigDir().resolve("visualsx-cosmetics.properties");
        }

        static void load() {
            if (loaded) return;
            loaded = true;
            try {
                Path f = file();
                if (!Files.exists(f)) return;
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(f)) { p.load(in); }
                for (int i = 0; i < NAMES.length; i++) {
                    ON[i] = Boolean.parseBoolean(p.getProperty("c" + i, "false"));
                }
            } catch (Exception ignored) { }
        }

        static void save() {
            try {
                Properties p = new Properties();
                for (int i = 0; i < NAMES.length; i++) p.setProperty("c" + i, String.valueOf(ON[i]));
                try (OutputStream out = Files.newOutputStream(file())) { p.store(out, "VisualsX cosmetics"); }
            } catch (Exception ignored) { }
        }

        static void set(int i, boolean v) {
            if (v) {
                int a = i < 3 ? 0 : 3;
                for (int k = a; k < a + 3; k++) ON[k] = false;
            }
            ON[i] = v;
            save();
        }
    }

    private static final String[] TABS = {"HUD", "Kamera", "Kosmetika"};
    private static int tab = 0;

    private static final int PANEL = 0x99101022;
    private static final int ACCENT = 0xFF7B4DFF;
    private static final int ORANGE = 0xFFFF7A45;
    private static final int ROW = 0x55000000;
    private static final int ROW_HOVER = 0x887B4DFF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFF9A90B8;

    private record Hit(int x, int y, int w, int h, Runnable action) { }

    private final List<Hit> hits = new ArrayList<>();
    private boolean wasDown;

    public MenuScreen() {
        super(Text.literal("_VisualsX_"));
        Cosmetics.load();
    }

    private static boolean mouseDown() {
        return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(),
            GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    @Override
    protected void init() {
        wasDown = mouseDown();
    }

    /** Xira fon yo'q: o'yin menyu orqasida aniq ko'rinadi. */
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x28000000);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        hits.clear();

        int pw = Math.min(400, width - 20);
        int ph = Math.min(200, height - 20);
        int px = (width - pw) / 2, py = (height - ph) / 2;

        // oyna: yarim shaffof shisha + yuqorida binafsha yorug'lik
        ctx.fill(px, py, px + pw, py + ph, PANEL);
        ctx.fillGradient(px, py, px + pw, py + 26, 0x667B4DFF, 0x00101022);
        border(ctx, px, py, pw, ph, 0x887B4DFF);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, py + 8, WHITE);

        // bo'limlar
        int tw = (pw - 20) / TABS.length;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            int tx = px + 10 + i * tw, ty = py + 26;
            boolean sel = i == tab;
            boolean hov = inside(mx, my, tx, ty, tw - 4, 18);
            ctx.fill(tx, ty, tx + tw - 4, ty + 18, sel ? 0x887B4DFF : (hov ? 0x665533AA : ROW));
            if (sel) ctx.fill(tx, ty + 16, tx + tw - 4, ty + 18, ORANGE);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(TABS[i]), tx + (tw - 4) / 2, ty + 5, WHITE);
            hits.add(new Hit(tx, ty, tw - 4, 18, () -> tab = idx));
        }

        int cw = (pw - 30) / 2;
        int left = px + 10, right = px + 20 + cw;
        int y = py + 56;

        if (tab == 0) {
            row(ctx, mx, my, left, y, cw, "Ping", () -> Config.ping, v -> Config.ping = v);
            row(ctx, mx, my, right, y, cw, "FPS / CPS / TPS", () -> Config.stats, v -> Config.stats = v);
            row(ctx, mx, my, left, y + 26, cw, "Logotip", () -> Config.logo, v -> Config.logo = v);
            row(ctx, mx, my, right, y + 26, cw, "WASD tugmalari", () -> Config.keys, v -> Config.keys = v);
            row(ctx, mx, my, left, y + 52, cw, "Potion effektlari", () -> Config.potions, v -> Config.potions = v);
            row(ctx, mx, my, right, y + 52, cw, "Orqa inventar 9x3", () -> Config.inventory, v -> Config.inventory = v);
        } else if (tab == 1) {
            row(ctx, mx, my, left, y, pw - 20, "Erkin kamera (F5)", () -> FreeLook.enabled, v -> FreeLook.enabled = v);
            ctx.drawTextWithShadow(textRenderer, "F5 da kamera alohida aylanadi, o'yinchi to'g'riga yuraveradi.", left, y + 34, GRAY);
            ctx.drawTextWithShadow(textRenderer, "V - o'yinchini kamera qaragan tomonga buradi.", left, y + 48, GRAY);
        } else {
            ctx.drawTextWithShadow(textRenderer, "Qanotlar", left, y - 2, ORANGE);
            ctx.drawTextWithShadow(textRenderer, "Shlyapalar", right, y - 2, ORANGE);
            for (int i = 0; i < 3; i++) {
                final int a = i, b = i + 3;
                row(ctx, mx, my, left, y + 12 + i * 26, cw, Cosmetics.NAMES[a], () -> Cosmetics.ON[a], v -> Cosmetics.set(a, v));
                row(ctx, mx, my, right, y + 12 + i * 26, cw, Cosmetics.NAMES[b], () -> Cosmetics.ON[b], v -> Cosmetics.set(b, v));
            }
            ctx.drawTextWithShadow(textRenderer, "Tanlov saqlanadi. Faqat sizga ko'rinadi (boshqalar ko'rmaydi).", left, y + 96, GRAY);
        }

        // chap tugma bosilishi
        boolean down = mouseDown();
        if (down && !wasDown) {
            for (Hit h : hits) {
                if (inside(mx, my, h.x(), h.y(), h.w(), h.h())) {
                    h.action().run();
                    break;
                }
            }
        }
        wasDown = down;
    }

    private void row(DrawContext ctx, int mx, int my, int x, int y, int w, String name,
                     BooleanSupplier get, Consumer<Boolean> set) {
        boolean on = get.getAsBoolean();
        boolean hov = inside(mx, my, x, y, w, 22);
        ctx.fill(x, y, x + w, y + 22, hov ? ROW_HOVER : ROW);
        ctx.fill(x, y, x + 2, y + 22, on ? ORANGE : 0x557B4DFF);
        ctx.drawTextWithShadow(textRenderer, name, x + 8, y + 7, on ? WHITE : GRAY);

        int sx = x + w - 30, sy = y + 6;
        ctx.fill(sx, sy, sx + 24, sy + 10, on ? ORANGE : 0xFF4A4A5C);
        int kx = on ? sx + 14 : sx + 1;
        ctx.fill(kx, sy + 1, kx + 9, sy + 9, WHITE);

        hits.add(new Hit(x, y, w, 22, () -> {
            set.accept(!get.getAsBoolean());
            Config.save();
        }));
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static void border(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y + 1, x + 1, y + h - 1, color);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }
}

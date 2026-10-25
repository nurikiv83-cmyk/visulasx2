package com.visualsx;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

public class Hud {
    private static final int WHITE = 0xFFFFFFFF, BLACK = 0xFF000000;
    private static final int BG = 0x88000000, ON = 0xBBFFFFFF;
    private static final ArrayDeque<Long> clicks = new ArrayDeque<>();
    private static boolean wasDown;
    private static long worldSince = -1;

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) { worldSince = -1; return; }
        TextRenderer tr = mc.textRenderer;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        long now = System.currentTimeMillis();
        if (worldSince < 0) worldSince = now;

        // CPS (chap tugma)
        boolean down = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (down && !wasDown) clicks.add(now);
        wasDown = down;
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.pollFirst();

        // Ping
        PlayerListEntry me = mc.getNetworkHandler() == null ? null : mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        int ping = me == null ? 0 : me.getLatency();

        box(ctx, tr, 4, 4, "Ping: " + ping);
        String top = mc.getCurrentFps() + " FPS  |  CPS: " + clicks.size() + "  |  TPS: " + String.format("%.1f", TpsTracker.tps);
        box(ctx, tr, (w - tr.getWidth(top) - 10) / 2, 4, top);

        // Logotip + qizil chiziq
        ctx.drawCenteredTextWithShadow(tr, "_VisualsX_", w / 2, 26, WHITE);
        ctx.fill(w / 2 - 60, 37, w / 2 + 60, 38, 0xFFFF4A6A);

        // WASD + Space
        int kx = 8, ky = h / 2 - 30;
        key(ctx, tr, "W", kx + 22, ky, 20, mc.options.forwardKey.isPressed());
        key(ctx, tr, "A", kx, ky + 22, 20, mc.options.leftKey.isPressed());
        key(ctx, tr, "S", kx + 22, ky + 22, 20, mc.options.backKey.isPressed());
        key(ctx, tr, "D", kx + 44, ky + 22, 20, mc.options.rightKey.isPressed());
        boolean jump = mc.options.jumpKey.isPressed();
        ctx.fill(kx, ky + 44, kx + 64, ky + 60, jump ? ON : BG);
        ctx.drawBorder(kx, ky + 44, 64, 16, WHITE);
        ctx.drawCenteredTextWithShadow(tr, "space", kx + 32, ky + 48, jump ? BLACK : WHITE);

        // Potion effektlari (o'ng tepada)
        int py = 40;
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            String name = Text.translatable(e.getTranslationKey()).getString() + " " + (e.getAmplifier() + 1);
            int s = e.getDuration() / 20;
            String time = e.isInfinite() ? "inf" : (s / 60) + ":" + String.format("%02d", s % 60);
            String line = name + " " + time;
            int bw = tr.getWidth(line) + 10;
            ctx.fill(w - bw - 4, py, w - 4, py + 16, BG);
            ctx.drawBorder(w - bw - 4, py, bw, 16, 0xFFB98CFF);
            ctx.drawTextWithShadow(tr, line, w - bw + 1, py + 4, WHITE);
            py += 19;
        }

        // Orqa inventar 9x3 (o'ng pastda): inventar slotlari 9..35
        int ix = w - 9 * 18 - 6, iy = h - 3 * 18 - 6;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int sx = ix + c * 18, sy = iy + r * 18;
                ctx.fill(sx, sy, sx + 18, sy + 18, BG);
                ctx.drawBorder(sx, sy, 18, 18, 0x66FFFFFF);
                ItemStack st = mc.player.getInventory().getStack(9 + r * 9 + c);
                if (!st.isEmpty()) {
                    ctx.drawItem(st, sx + 1, sy + 1);
                    ctx.drawStackOverlay(tr, st, sx + 1, sy + 1);
                }
            }
        }

        // Mixin ishlamasa ogohlantirish (versiya mos kelmasa)
        if (now - worldSince > 5000 && (!FreeLook.cameraHooked || !FreeLook.mouseHooked)) {
            ctx.drawTextWithShadow(tr, "VisualsX: erkin kamera ishlamayapti (mixin)", 4, h - 12, 0xFFFF5555);
        }
    }

    private static void box(DrawContext ctx, TextRenderer tr, int x, int y, String text) {
        int bw = tr.getWidth(text) + 10;
        ctx.fill(x, y, x + bw, y + 16, BG);
        ctx.drawBorder(x, y, bw, 16, WHITE);
        ctx.drawTextWithShadow(tr, text, x + 5, y + 4, WHITE);
    }

    private static void key(DrawContext ctx, TextRenderer tr, String t, int x, int y, int s, boolean on) {
        ctx.fill(x, y, x + s, y + s, on ? ON : BG);
        ctx.drawBorder(x, y, s, s, WHITE);
        ctx.drawCenteredTextWithShadow(tr, t, x + s / 2, y + 6, on ? BLACK : WHITE);
    }
}

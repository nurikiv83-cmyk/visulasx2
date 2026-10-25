package com.visualsx;

import net.minecraft.client.MinecraftClient;

/** Serverdan vaqt yangilanishi kelganda joriy TPS ni hisoblaydi. */
public class TpsTracker {
    public static double tps = 20.0;
    private static long lastTime = -1, lastNano;

    public static void onTimeUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        long worldTime = mc.world.getTime();
        long now = System.nanoTime();
        if (lastTime >= 0) {
            double sec = (now - lastNano) / 1e9;
            if (sec > 0.2) tps = Math.max(0, Math.min(20, (worldTime - lastTime) / sec));
        }
        lastTime = worldTime; lastNano = now;
    }
}

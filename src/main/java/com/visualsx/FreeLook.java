package com.visualsx;

import net.minecraft.client.MinecraftClient;

/** F5 da kamera alohida aylanadi, o'yinchi yo'nalishi o'zgarmaydi. */
public class FreeLook {
    public static boolean enabled = true;
    public static float yaw, pitch;
    /** Mixinlar haqiqatan ishga tushganini bilish uchun (versiya mos kelmasa o'chib qoladi). */
    public static volatile boolean cameraHooked, mouseHooked;

    public static boolean active() {
        MinecraftClient c = MinecraftClient.getInstance();
        return enabled && cameraHooked && mouseHooked && c.player != null
            && !c.options.getPerspective().isFirstPerson();
    }

    public static void turn(double dx, double dy) {
        yaw += (float) (dx * 0.15);
        pitch = Math.max(-90f, Math.min(90f, pitch + (float) (dy * 0.15)));
    }

    public static void follow() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player != null) { yaw = c.player.getYaw(); pitch = c.player.getPitch(); }
    }
}

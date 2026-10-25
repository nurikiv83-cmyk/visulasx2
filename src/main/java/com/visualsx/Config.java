package com.visualsx;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Menyudagi sozlamalar. config/visualsx.properties faylida saqlanadi. */
public class Config {
    public static boolean ping = true, stats = true, logo = true,
        keys = true, potions = true, inventory = true;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("visualsx.properties");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(f)) { p.load(in); }
            ping = get(p, "ping"); stats = get(p, "stats"); logo = get(p, "logo");
            keys = get(p, "keys"); potions = get(p, "potions"); inventory = get(p, "inventory");
            FreeLook.enabled = get(p, "freelook");
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            p.setProperty("ping", String.valueOf(ping));
            p.setProperty("stats", String.valueOf(stats));
            p.setProperty("logo", String.valueOf(logo));
            p.setProperty("keys", String.valueOf(keys));
            p.setProperty("potions", String.valueOf(potions));
            p.setProperty("inventory", String.valueOf(inventory));
            p.setProperty("freelook", String.valueOf(FreeLook.enabled));
            try (OutputStream out = Files.newOutputStream(file())) { p.store(out, "VisualsX"); }
        } catch (Exception ignored) { }
    }

    private static boolean get(Properties p, String k) {
        return Boolean.parseBoolean(p.getProperty(k, "true"));
    }
}

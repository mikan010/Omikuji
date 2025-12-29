package com.github.siloneco.omikuji.utility;

import java.lang.reflect.InvocationTargetException;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class VersionUtils {

    private static final int VERSION = detectMinorVersion();

    private static int detectMinorVersion() {
        try {
            // OldType: org.bukkit.craftbukkit.v1_16_R3 -> 16
            String pkg = Bukkit.getServer().getClass().getPackage().getName();
            String[] parts = pkg.split("\\.");
            if (parts.length >= 4 && parts[3].startsWith("v1_")) {
                String v = parts[3];    // v1_16_R3
                String[] v2 = v.substring(2).split("_");
                return Integer.parseInt(v2[0]);    // "16_R3" -> ["16", "R3"]
            }
        } catch (Throwable ignored) {
        }

        // NewType: Bukkit.getMinecraftVersion() = "1.21.11" -> 21
        try {
            String mc = Bukkit.getMinecraftVersion();
            String[] vs = mc.split("\\.");
            if (vs.length >= 2) {return Integer.parseInt(vs[1]);}
        } catch (Throwable ignored) {
        }

        return 21;
    }

    public static int getVersion() {
        return VERSION;
    }

    public static String getInventoryTitle(InventoryView view, Inventory inv) {
        if (VERSION >= 14) {
            return view.getTitle();
        } else {
            try {
                return (String) Inventory.class.getMethod("getTitle").invoke(inv);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    public static boolean isSign(Block block) {
        if (VERSION >= 13) {
            return block.getType().toString().endsWith("_SIGN");
        } else {
            return block.getType() == Material.valueOf("SIGN_POST") || block.getType() == Material.valueOf("WALL_SIGN");
        }
    }

    public static void playLevelUpSound(Player p) {
        Sound sound;
        if (VERSION > 8) {
            sound = Sound.ENTITY_PLAYER_LEVELUP;
        } else {
            sound = Sound.valueOf("LEVEL_UP");
        }
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void sendTitle(String title, int fadeIn, int stay, int fadeOut, Player... players) {
        if (VERSION <= 18) {
            JSONMessage.create(title).title(0, 20, 10, players);
        } else {
            for (Player p : players) {
                p.sendTitle(title, "", fadeIn, stay, fadeOut);
            }
        }
    }
}

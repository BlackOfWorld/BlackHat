package me.bow.treecapitatorultimate.Utils;

import org.bukkit.Bukkit;

public final class CraftBukkitUtil {
    private static final String SERVER_PACKAGE_VERSION = getServerPackageVersion();
    private static final boolean CHAT_COMPATIBLE = !SERVER_PACKAGE_VERSION.startsWith(".v1_7_");

    private CraftBukkitUtil() {
    }

    private static String getServerPackageVersion() {
        Class<?> server = Bukkit.getServer().getClass();
        if (!server.getSimpleName().equals("CraftServer")) {
            return ".";
        }
        if (server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            // Non versioned class
            return ".";
        } else {
            String version = server.getName().substring("org.bukkit.craftbukkit".length());
            return version.substring(0, version.length() - "CraftServer".length());
        }
    }

    public static boolean isChatCompatible() {
        return CHAT_COMPATIBLE;
    }

    public static String nms(String className) {
        return "net.minecraft.server" + SERVER_PACKAGE_VERSION + className;
    }

    public static Class<?> nmsClass(String className) throws ClassNotFoundException {
        return Class.forName(nms(className));
    }

    public static String obc(String className) {
        return "org.bukkit.craftbukkit" + SERVER_PACKAGE_VERSION + className;
    }

    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obc(className));
    }
}

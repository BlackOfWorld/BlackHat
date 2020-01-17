package me.bow.treecapitatorultimate.Utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CraftBukkitUtil {
    public static Object getNmsPlayer(Player p) {
        try {
            return p.getClass().getMethod("getHandle").invoke(p);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player p, Object... args) {
        final Object nmsPlayer = getNmsPlayer(p);
        try {
            final Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            final Object pConnection = playerConnectionField.get(nmsPlayer);
            Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
            final Method sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
            sendPacket.invoke(pConnection, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static byte getEntityMetadata(boolean onFire, boolean crouched, boolean sprinting, boolean swimming, boolean invisible, boolean glowing, boolean usingElytra) {
        byte index0 = 0;
        if (onFire)
            index0 += 1;
        if (crouched)
            index0 += 2;
        if (sprinting)
            index0 += 8;
        if (swimming)
            index0 += 16;
        if (invisible)
            index0 += 32;
        if (glowing)
            index0 += 64;
        if (usingElytra)
            index0 += 128;
        return index0;
    }
}

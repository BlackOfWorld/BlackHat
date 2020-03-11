package me.bow.treecapitatorultimate.Utils;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public final class CraftBukkitUtil {
    private static final Class<?> CRAFT_PLAYER = Objects.requireNonNull(ReflectionUtils.getClass("{obc}.entity.CraftPlayer"));
    private static final Class<?> CRAFT_SERVER = Objects.requireNonNull(ReflectionUtils.getClass("{obc}.CraftServer"));

    public static Object getNmsPlayer(Player p) {
        try {
            return p.getClass().getMethod("getHandle").invoke(p);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getObcPlayer(Player p) {
        return CRAFT_PLAYER.cast(p);
    }

    public static Object getNmsEntity(Entity e) {
        try {
            return e.getClass().getMethod("getHandle").invoke(e);
        } catch (IllegalAccessException | NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object getNmsItemStack(ItemStack item) {
        final Class<?> craftItemStack = ReflectionUtils.getClassCached("{obc}.inventory.CraftItemStack");
        try {
            return ReflectionUtils.getMethodCached(craftItemStack, "asNMSCopy", ItemStack.class).invoke(craftItemStack, item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getNmsServer() {
        Object o = null;
        try {
            Server server = Bukkit.getServer();
            o = ReflectionUtils.getMethod(server.getClass(), "getServer", 0).invoke(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static Object getDedicatedServerPropertiesInstance() throws InvocationTargetException, IllegalAccessException {
        Object server = getNmsServer();
        Field propertyManager = ReflectionUtils.getField(server.getClass(), "propertyManager");
        Class<?> serverSettings = ReflectionUtils.getClass("{nms}.DedicatedServerSettings");
        Object dedicatedSettingsInstance = propertyManager.get(server);
        return ReflectionUtils.getMethod(serverSettings, "getProperties", 0).invoke(dedicatedSettingsInstance);
    }

    public static Server getBukkitServer() {
        return Start.Instance.getServer();
    }

    public static Object getObcServer() {
        return CRAFT_SERVER.cast(getBukkitServer());
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

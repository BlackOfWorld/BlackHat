package me.bow.treecapitatorultimate.Utils;

import com.mojang.authlib.GameProfile;
import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

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

    public static Object getNmsWorld(World world) throws InvocationTargetException, IllegalAccessException {
        return ReflectionUtils.getMethod(ReflectionUtils.getClassCached("{obc}.CraftWorld"), "getHandle", 0).invoke(world);
    }

    public static Player getOfflinePlayer(String name, UUID uuid, Location location) {
        try {
            File playerFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

            for (File playerFile : playerFolder.listFiles()) {
                String fileName = playerFile.getName();
                String playerName = fileName.substring(0, fileName.length() - 4);

                GameProfile profile = new GameProfile(uuid, playerName);

                if (playerName.trim().equalsIgnoreCase(name)) {
                    Object server = getNmsServer();
                    Object worldServer = CraftBukkitUtil.getNmsWorld(location.getWorld());
                    Object entityPlayer = ReflectionUtils.getConstructor("{nms}.EntityPlayer", (Class<?>[]) null).invoke(server, worldServer, profile, ReflectionUtils.getConstructor("{nms}.PlayerInteractManager", (Class<?>[]) null).invoke(worldServer));

                    ReflectionUtils.getMethodCached(entityPlayer.getClass(), "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                    ReflectionUtils.getFieldCached(entityPlayer.getClass(), "world").set(entityPlayer, worldServer);
                    Player target = entityPlayer == null ? null : (Player) ReflectionUtils.getMethodCached(entityPlayer.getClass(), "getBukkitEntity").invoke(entityPlayer);
                    if (target != null) {
                        //target.load();
                        return target;
                    }
                }
            }

        } catch (Exception e) {
        }
        return null;
    }

    public static void refreshPlayer(Player p) throws InvocationTargetException, IllegalAccessException {
        //slow but works
        Object craftServer = CraftBukkitUtil.getObcServer();
        craftServer = ReflectionUtils.getMethodCached(craftServer.getClass(), "getHandle").invoke(craftServer);
        Object nmsPlayer = CraftBukkitUtil.getNmsPlayer(p);
        Object dimensionManager = ReflectionUtils.getFieldCached(nmsPlayer.getClass(), "dimension").get(nmsPlayer);
        ReflectionUtils.getMethodCached(craftServer.getClass(), "moveToWorld", nmsPlayer.getClass(), ReflectionUtils.getClassCached("{nms}.DimensionManager"), boolean.class, Location.class, boolean.class).invoke(craftServer, nmsPlayer, dimensionManager, false, p.getLocation(), false);
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

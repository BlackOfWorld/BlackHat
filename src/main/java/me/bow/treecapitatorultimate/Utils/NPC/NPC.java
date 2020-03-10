package me.bow.treecapitatorultimate.Utils.NPC;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class NPC {

    //https://github.com/thepn/ReplayAPI/blob/master/src/de/sebpas/replay/npc/NPC.java
    private final String name;
    private final GameProfile gameProfile;
    private final String texture;
    private final String signature;
    private final Class<?> craftServerClass = ReflectionUtils.getClassCached("{obc}.CraftWorld");
    private Location location;
    private Object entityPlayer;
    private int entityID;
    private boolean isSpawned;
    private HashSet<UUID> hiddenPlayers = new HashSet<>();

    public NPC(Location location, String name, String texture, String signature) {
        this.location = location;
        this.name = name;
        this.gameProfile = new GameProfile(UUID.randomUUID(), this.name);
        this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        this.texture = texture;
        this.signature = signature;
    }

    public void Despawn() {
        if (!isSpawned) return;
        int[] array = {this.entityID};
        Packet packetPlayOutEntityDestroy = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityDestroy", int[].class).invoke(array));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (this.hiddenPlayers.contains(p.getUniqueId())) continue;
            try {
                PacketSender.Instance.sendPacket(p, packetPlayOutEntityDestroy);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void Spawn() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (isSpawned) return;
        Object mcServer = Start.GetServer();
        Object worldServer = ReflectionUtils.getMethod(craftServerClass, "getHandle", 0).invoke(location.getWorld());

        this.entityPlayer = ReflectionUtils.getConstructor("{nms}.EntityPlayer", null).invoke(mcServer, worldServer, this.gameProfile, ReflectionUtils.getConstructor("{nms}.PlayerInteractManager", null).invoke(worldServer));
        this.entityID = (int) ReflectionUtils.getField(this.entityPlayer.getClass(),"id").get(this.entityPlayer);
        ReflectionUtils.getMethodCached(this.entityPlayer.getClass(), "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        isSpawned = true;
    }

    protected void Refresh() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Object addPlayer = ReflectionUtils.getEnumVariable("{nms}.PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "ADD_PLAYER");
        Object removePlayer = ReflectionUtils.getEnumVariable("{nms}.PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "REMOVE_PLAYER");
        Object array = Array.newInstance(this.entityPlayer.getClass(), 1);
        Array.set(array, 0, this.entityPlayer);

        Packet packetPlayOutPlayerInfoAdd = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutPlayerInfo", addPlayer.getClass(), ReflectionUtils.getClassCached("[L{nms}.EntityPlayer;")).invoke(addPlayer, array));

        Packet packetPlayOutNamedEntitySpawn = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutNamedEntitySpawn", ReflectionUtils.getClassCached("{nms}.EntityHuman")).invoke(this.entityPlayer));

        float yaw = (float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
        Packet packetPlayOutEntityHeadRotation = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityHeadRotation", ReflectionUtils.getClassCached("{nms}.Entity"), byte.class).invoke(this.entityPlayer, (byte) (yaw * 256 / 360)));

        Packet packetPlayOutPlayerInfoRemove = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutPlayerInfo", addPlayer.getClass(), ReflectionUtils.getClassCached("[L{nms}.EntityPlayer;")).invoke(removePlayer, array));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isOnline()) continue;
            if (this.hiddenPlayers.contains(p.getUniqueId())) continue;
            PacketSender.Instance.sendPacket(p, packetPlayOutPlayerInfoAdd, packetPlayOutNamedEntitySpawn, packetPlayOutEntityHeadRotation, packetPlayOutPlayerInfoRemove);
        }
    }

    public void Show(Player... players) {
        for (Player p : players) {
            if (!this.hiddenPlayers.contains(p.getUniqueId())) continue;
            this.hiddenPlayers.remove(p.getUniqueId());
        }
        try {
            Refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Hide(Player... players) {
        for (Player p : players) {
            if (this.hiddenPlayers.contains(p.getUniqueId())) continue;
            this.hiddenPlayers.add(p.getUniqueId());
        }
        try {
            Refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LookAt(float yaw, float pitch, Player... players) {
        setLocation(location.getX(), location.getY(), location.getZ(), yaw, pitch);
        Packet packetPlayOutEntityHeadRotation = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityHeadRotation", ReflectionUtils.getClassCached("{nms}.Entity"), byte.class).invoke(this.entityPlayer, (byte) (yaw * 256 / 360)));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (this.hiddenPlayers.contains(p.getUniqueId())) continue;
            try {
                PacketSender.Instance.sendPacket(p, packetPlayOutEntityHeadRotation);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<Player> getHiddenPlayers() {
        Set<Player> ret = new HashSet();
        Iterator var3 = this.hiddenPlayers.iterator();

        while (var3.hasNext()) {
            UUID u = (UUID) var3.next();
            ret.add(Bukkit.getServer().getPlayer(u));
        }

        return Collections.unmodifiableSet(ret);
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.contains(player.getUniqueId());
    }

    private void setLocation(Location location) {
        this.location = location;
        try {
            ReflectionUtils.getMethodCached(this.entityPlayer.getClass(), "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setLocation(double x, double y, double z, float yaw, float pitch) {
        setLocation(new Location(location.getWorld(), x, y, z, yaw, pitch));
    }
}

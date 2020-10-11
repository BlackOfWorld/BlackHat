package me.bow.treecapitatorultimate.Utils.NPC;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class NPC {

    //https://github.com/thepn/ReplayAPI/blob/master/src/de/sebpas/replay/npc/NPC.java
    private final String name;
    private final GameProfile gameProfile;
    private final String texture;
    private final String signature;
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

    public void Despawn() throws InvocationTargetException, IllegalAccessException {
        if (!isSpawned) return;
        int[] array = {this.entityID};
        Packet packetPlayOutEntityDestroy = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityDestroy", int[].class).invoke(array));
        sendPacketAll(packetPlayOutEntityDestroy);
    }

    public void Spawn() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (isSpawned) return;
        Object mcServer = CraftBukkitUtil.getNmsServer();
        Object worldServer = CraftBukkitUtil.getNmsWorld(location.getWorld());

        this.entityPlayer = ReflectionUtils.getConstructor("{nms}.EntityPlayer", (Class<?>[]) null).invoke(mcServer, worldServer, this.gameProfile, ReflectionUtils.getConstructor("{nms}.PlayerInteractManager", (Class<?>[]) null).invoke(worldServer));
        this.entityID = (int) ReflectionUtils.getField(this.entityPlayer.getClass(), "id").get(this.entityPlayer);
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
        sendPacketAll(packetPlayOutPlayerInfoAdd, packetPlayOutNamedEntitySpawn, packetPlayOutEntityHeadRotation, packetPlayOutPlayerInfoRemove);
    }

    private void sendPacketAll(Packet... packets) throws InvocationTargetException, IllegalAccessException {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isOnline()) continue;
            if (this.hiddenPlayers.contains(p.getUniqueId())) continue;
            PacketSender.Instance.sendPacket(p, packets);
        }
    }

    public double getEyeHeight(boolean ignorePose) throws InvocationTargetException, IllegalAccessException {
        return ignorePose ? 1.62D : this.getEyeHeight();
    }

    public float getEyeHeight() throws InvocationTargetException, IllegalAccessException {
        return (float) ReflectionUtils.getMethodCached(this.entityPlayer.getClass(), "getHeadHeight").invoke(this.entityPlayer);
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

    public void LookAt(Vector vector) throws InvocationTargetException, IllegalAccessException {
        Vector location = this.location.toVector().clone();
        location.setY(location.getY() + this.getEyeHeight());
        Vector delta = vector.clone().subtract(location).normalize();
        double pitch = -Math.atan(delta.getY() / Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ())) * (180 / Math.PI);
        double yaw = Math.atan2(delta.getZ(), delta.getX()) * (180 / Math.PI) - 90;
        this.Look((float) (yaw), (float) (pitch));
    }

    public void Look(float yaw, float pitch) throws InvocationTargetException, IllegalAccessException {
        setLocation(location.getX(), location.getY(), location.getZ(), yaw, pitch);
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

    public void Attack(Entity entity) {
        Packet packet = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutAnimation", ReflectionUtils.getClassCached("{nms}.Entity"), int.class).invoke(entityPlayer, 0));
        Packet packet2 = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutAnimation", ReflectionUtils.getClassCached("{nms}.Entity"), int.class).invoke(CraftBukkitUtil.getNmsEntity(entity), 1));
        try {
            sendPacketAll(packet, packet2);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void Teleport(Location loc) {
        setLocation(loc);
    }

    private void setLocation(Location location) {
        this.location = location;
        try {
            ReflectionUtils.getMethodCached(this.entityPlayer.getClass(), "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            Packet packetPlayOutEntityTeleport = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityTeleport", ReflectionUtils.getClassCached("{nms}.Entity")).invoke(this.entityPlayer));
            //Packet packetPlayOutEntityLook = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntity$PacketPlayOutEntityLook", int.class, byte.class, byte.class, boolean.class).invoke(entityID, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)), (byte) ((int) (location.getPitch() * 256.0F / 360.0F)), true));
            Packet packetPlayOutEntityHeadRotation = Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutEntityHeadRotation", ReflectionUtils.getClassCached("{nms}.Entity"), byte.class).invoke(this.entityPlayer, (byte) (this.location.getYaw() * 256 / 360)));
            sendPacketAll(packetPlayOutEntityTeleport, packetPlayOutEntityHeadRotation);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setLocation(double x, double y, double z, float yaw, float pitch) {
        setLocation(new Location(location.getWorld(), x, y, z, yaw, pitch));
    }
}

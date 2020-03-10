package me.bow.treecapitatorultimate.Utils.Packet;

import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A Packet sender
 */
public class PacketSender {

    private static final Class<?> CRAFT_PLAYER =
            Objects.requireNonNull(ReflectionUtils.getClass("{obc}.entity.CraftPlayer"));
    private static final Class<?> PLAYER_CONNECTION =
            Objects.requireNonNull(ReflectionUtils.getClass("{nms}.PlayerConnection"));
    private static final Class<?> ENTITY_PLAYER =
            Objects.requireNonNull(ReflectionUtils.getClass("{nms}.EntityPlayer"));


    private static final Method GET_HANDLE = ReflectionUtils.getMethod(
            CRAFT_PLAYER, "getHandle", -1
    );
    private static final Method SEND_PACKET = ReflectionUtils.getMethod(
            PLAYER_CONNECTION, "sendPacket", -1
    );
    private static final Field PLAYER_CONNECTION_FIELD = ReflectionUtils.getField(
            ENTITY_PLAYER, "playerConnection"
    );

    public static PacketSender Instance = new PacketSender();

    private PacketSender() {
        Instance = this;
    }

    /**
     * Sends a packet to a Player
     *
     * @param packets {@link Packet}s to send
     * @param player The Player to send it to
     */
    public void sendPacket(Player player, Packet... packets) throws InvocationTargetException, IllegalAccessException {
        for (Packet packet: packets) {
            sendPacket(packet.getNMSPacket(), getConnection(player));
        }
    }

    private void sendPacket(Object nmsPacket, Object playerConnection) throws InvocationTargetException, IllegalAccessException {
        SEND_PACKET.invoke(playerConnection, nmsPacket);
    }

    /**
     * Returns the Player's PlayerConnection
     *
     * @param player The Player to get the Connection for
     * @return The Player's connection
     */
    public Object getConnection(Player player) throws InvocationTargetException, IllegalAccessException {
        Object handle = GET_HANDLE.invoke(player);

        return PLAYER_CONNECTION_FIELD.get(handle);
    }
}

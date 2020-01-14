package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class EndScreen extends Command {
    public EndScreen() {
        super("endscreen", "What?! You defeated the enderdragon already?!", CommandCategory.Player, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            Object packetPlayOutGameStateChange;
            final Class<?> packetPlayOutGameStateChangeClass = ReflectionUtils.getMinecraftClass("PacketPlayOutGameStateChange");
            final Object nmsPlayer = Player.class.getMethod("getHandle").invoke(anotherPlayer);
            final Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            final Object pConnection = playerConnectionField.get(nmsPlayer);
            Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
            final Method sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
            packetPlayOutGameStateChange = ReflectionUtils.getConstructorCached(packetPlayOutGameStateChangeClass, int.class, float.class).invoke(4, 1);
            sendPacket.invoke(pConnection, packetPlayOutGameStateChange);
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + " now has end screen!");
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

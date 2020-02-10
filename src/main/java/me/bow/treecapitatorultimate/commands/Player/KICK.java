package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "kick", description = "Kicks player with additional reason", category = CommandCategory.Player, requiredArgs = 1)
public class KICK extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (args.size() == 1) {
                anotherPlayer.kickPlayer("Kicked from server.");
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " got kicked (without reason)!");
            } else {
                String reason = "";
                for (int i = 1; i < args.size(); i++) {
                    reason += args.get(i) + " ";
                }
                reason = reason.replace("&", "§").replace("\\n", "\n").replace("|", "\n");
                anotherPlayer.kickPlayer(reason);
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " got kicked (with reason)!");
            }
        } catch (Exception e) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Player is not online!");
        }
    }
}

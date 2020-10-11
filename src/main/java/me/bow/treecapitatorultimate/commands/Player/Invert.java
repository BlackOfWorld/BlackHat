package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "invert", description = "Inverts fly player movement", category = CommandCategory.Player, requiredArgs = 1)
public class Invert extends Command {
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        //p.setMetadata();
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (args.size() == 1) {
                anotherPlayer.setFlySpeed(anotherPlayer.getFlySpeed() * -1.0F);
                p.Reply( ChatColor.BLUE + anotherPlayer.getName() + "'s fly movement was inverted!");
                p.Notify(ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " inverted "+  ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + "'s fly movement!");
            }
        } catch (Exception e) {
            p.Reply(ChatColor.RED + "Player is not online!");
        }
    }
}

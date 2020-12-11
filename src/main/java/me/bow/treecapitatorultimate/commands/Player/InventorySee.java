package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

//TODO: make it better - https://github.com/lishid/OpenInv
@Command.Info(command = "invsee", description = "Opens inventory", category = CommandCategory.Player, requiredArgs = 1)
public class InventorySee extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.Error(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (args.size() == 1) {
                p.openInventory(anotherPlayer.getInventory());
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + "'s inventory was opened!");
            }
        } catch (Exception e) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Player is not online!");
        }
    }
}

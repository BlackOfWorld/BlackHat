package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "minimalisticMC", description = "Minimalistic minecraft - (removes inventory, almost everything tbh)", category = CommandCategory.Player, requiredArgs = 1)
public class minimalisticMinecraft extends Command {
    ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onServerTick() {
        for (UUID playerName : players) {
            Player p = Bukkit.getPlayer(playerName);
            if ((p == null || !p.isOnline())) break;
            p.closeInventory();
            p.setLevel(0);
            p.getInventory().clear();
            p.setPortalCooldown(999);
            p.setWalkSpeed(0.025f);
            p.setFlying(false);
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player is not online!");
                return;
            }
            if (!players.contains(anotherPlayer.getUniqueId())) {
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " now has minimalistic minecraft!");
                players.add(anotherPlayer.getUniqueId());
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " now longer has minimalitic minecraft!");
            players.remove(anotherPlayer.getUniqueId());
            anotherPlayer.setWalkSpeed(0.2f);
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }
}

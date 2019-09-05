package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class Swim extends Command {
    ArrayList<UUID> players = new ArrayList<>();

    public Swim() {
        super("swim", "Player will have swim animation, even on land!", CommandCategory.Player, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() == 0) return;
                for (UUID i : players) {
                    Player p = Bukkit.getPlayer(i);
                    if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR || !p.isOnGround())
                        return;
                    p.setSwimming(true);
                }
            }
        }.runTaskTimer(Start.Instance, 0, 5);
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
                anotherPlayer.setSwimming(true);
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " is now swimming!");
                players.add(anotherPlayer.getUniqueId());
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " is now longer swimming!");
            players.remove(anotherPlayer.getUniqueId());
            anotherPlayer.setSwimming(false);
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }

    @Override
    public void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        if (!players.contains(e.getEntity().getUniqueId())) return;
        if (e.isSwimming()) return;
        e.setCancelled(true);
    }
}

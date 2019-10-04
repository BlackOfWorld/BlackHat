package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class BHop extends Command {
    private ArrayList<UUID> players = new ArrayList<>();

    public BHop() {
        super("bhop", "Bunnyhop like it's easter!", CommandCategory.Miscs);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer BHopping!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now BHopping!");
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        Location from = e.getFrom();
        if (from.getBlock().equals(Objects.requireNonNull(e.getTo()).getBlock())) return;
        if (!p.isOnGround()) return;
        p.setSprinting(true);
        Vector velocity = p.getVelocity();
        Bukkit.broadcastMessage(Start.Prefix + ChatColor.RED + "[DEBUG] " + velocity.toString());
        p.setVelocity(velocity.setY(0.4199999868869782D));
    }
}

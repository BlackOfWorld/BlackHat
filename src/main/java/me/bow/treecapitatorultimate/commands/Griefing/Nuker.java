package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;

public class Nuker extends Command implements Listener {
    ArrayList<nukerInfo> griefPlayers = new ArrayList<>();

    public Nuker() {
        super("nuker", "Breaks blocks around you", CommandCategory.Griefing, 0);
    }

    public int isPart(UUID uuid) {
        for (int i = 0; i < griefPlayers.size(); i++)
            if (griefPlayers.get(i).player.equals(uuid))
                return i;
        return -1;
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        int index = isPart(p.getUniqueId());
        if (index != -1) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Nuker disabled!");
            griefPlayers.remove(index);
        } else {
            if (args.size() == 0) {
                p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments! (" + args.size() + " out of " + this.getRequiredArgs() + ")");
                return;
            }
            int range;
            try {
                range = Integer.parseInt(args.get(0));
            } catch (Exception e2) {
                Start.ErrorException(p, e2);
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Nuker enabled!");
            nukerInfo info = new nukerInfo(p.getUniqueId(), range);
            griefPlayers.add(info);
        }
    }


    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        int index = isPart(e.getPlayer().getUniqueId());
        if (index == -1) return;
        nukerInfo info = griefPlayers.get(index);
        Player p = e.getPlayer();
        Location l = p.getLocation();
        for (double x = l.getBlockX() - info.range; x <= l.getBlockX() + info.range; x++)
            for (double y = l.getBlockY() - info.range; y <= l.getBlockY() + info.range; y++)
                for (double z = l.getBlockZ() - info.range; z <= l.getBlockZ() + info.range; z++) {
                    Location lc = new Location(p.getWorld(), x, y, z);
                    try {
                        if (lc.getBlock().getType().equals(Material.AIR)) continue;
                        lc.getBlock().setType(Material.AIR);
                    } catch (Exception e2) {
                        Start.ErrorException(p, e2);
                        griefPlayers.remove(info.player);
                    }
                }
    }

    final class nukerInfo {
        UUID player;
        int range;

        public nukerInfo(UUID player, int range) {
            this.player = player;
            this.range = range;
        }
    }
}

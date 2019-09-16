package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.UUID;

public class ForceField extends Command {
    private ArrayList<forceField> players = new ArrayList<>();
    private int tick = 0;

    public ForceField() {
        super("forcefield", "Bruh, how are you hitting me that far away?", CommandCategory.Griefing, 0);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        int index = isPart(p.getUniqueId());
        switch (args.get(0)) {
            case "on":
                if (index == -1) {
                    players.add(new forceField(p.getUniqueId()));
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + "Successfully turned on FF!");
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You already have FF on!");
                }
                break;
            case "off":
                if (index != -1) {
                    players.remove(index);
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + "Successfully turned off FF!");
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You already have FF off!");
                }
                break;
            case "hostile":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitHostileMobs = !ff.hitHostileMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit hostile mobs: " + (ff.hitHostileMobs ? "On" : "Off"));
                    players.set(index, ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "friendly":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitFriendlyMobs = !ff.hitFriendlyMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit friendly mobs: " + (ff.hitFriendlyMobs ? "On" : "Off"));
                    players.set(index, ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "players":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitPlayers = !ff.hitPlayers;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit players: " + (ff.hitPlayers ? "On" : "Off"));
                    players.set(index, ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "range":
                if (args.size() < 2) {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments!");
                    break;
                }
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.range = Double.parseDouble(args.get(1));
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Range: " + ff.range);
                    players.set(index, ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            default:
                // handle this idk
        }
    }

    @Override
    public void onServerTick() {
        if (tick++ < 5) return;
        tick = 0;
        for (forceField ff : players) {
            Player p = Bukkit.getPlayer(ff.player);
            if (p == null || !p.isOnline()) continue;
            if (p.getGameMode() == GameMode.SPECTATOR) continue;
            for (Entity ps : p.getNearbyEntities(ff.range, ff.range, ff.range))
                hitEntity(p, ps, ff.hitPlayers, ff.hitHostileMobs, ff.hitFriendlyMobs);
        }
    }

    private void hitEntity(Player p, Entity e, boolean damagePlayer, boolean hitHostileMobs, boolean hitFriendlyMobs) {
        if (!(e instanceof Monster || e instanceof Flying || e instanceof Ageable || e instanceof HumanEntity)) return;
        if (hitFriendlyMobs && e instanceof Ageable) {
            ((CraftPlayer) p).getHandle().attack(((CraftEntity) e).getHandle());
            (((CraftPlayer) p).getHandle()).playerConnection.sendPacket(new PacketPlayOutAnimation(((CraftPlayer) p).getHandle(), 0));  // required for their client to see it too
        }
        if (hitHostileMobs && (e instanceof Monster || e instanceof Flying)) {
            ((CraftPlayer) p).getHandle().attack(((CraftEntity) e).getHandle());
            (((CraftPlayer) p).getHandle()).playerConnection.sendPacket(new PacketPlayOutAnimation(((CraftPlayer) p).getHandle(), 0));  // required for their client to see it too
        }
        if (damagePlayer && e instanceof HumanEntity) {
            ((CraftPlayer) p).getHandle().attack(((CraftEntity) e).getHandle());
            (((CraftPlayer) p).getHandle()).playerConnection.sendPacket(new PacketPlayOutAnimation(((CraftPlayer) p).getHandle(), 0));  // required for their client to see it too
        }
    }

    private int isPart(UUID uuid) {
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).player.equals(uuid))
                return i;
        return -1;
    }

    final class forceField {
        UUID player;
        double range = 6.0d;
        boolean hitPlayers = true;
        boolean hitHostileMobs = false;
        boolean hitFriendlyMobs = false;
        forceField(UUID player) {
            this.player = player;
        }
    }
}
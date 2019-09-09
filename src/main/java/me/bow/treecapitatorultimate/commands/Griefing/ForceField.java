package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.UUID;

public class ForceField extends Command {
    //TODO: implement this: https://www.spigotmc.org/resources/voodoodoll.32550
    ArrayList<forceField> players = new ArrayList<>();

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
            case "mobs":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitMobs = !ff.hitMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit mobs: "+ (ff.hitMobs ? "On" : "Off"));
                    players.set(index,ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "players":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitPlayers = !ff.hitPlayers;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit players: "+ (ff.hitPlayers ? "On" : "Off"));
                    players.set(index,ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "range":
                if(args.size() < 2) {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments!");
                    break;}
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.range = Double.parseDouble(args.get(1));
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Range: "+ff.range);
                    players.set(index,ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            default:
                // handle this idk
        }
    }
    int tick = 0;
    @Override
    public void onServerTick() {
        if(tick++ < 5) return;
        tick = 0;
        for(forceField ff : players)
        {
            Player p = Bukkit.getPlayer(ff.player);
            p.getDama
            for (Entity ps : p.getNearbyEntities(ff.range,ff.range,ff.range)){
                if (ps instanceof HumanEntity) {
                    HumanEntity pps = (HumanEntity)ps;
                    pps.damage();
                    Bukkit.getPluginManager().callEvent(new EntityDamageEvent(pps, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 100));
                }
            }
        }
    }

    public int isPart(UUID uuid) {
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).player.equals(uuid))
                return i;
        return -1;
    }

    final class forceField {
        UUID player;
        double range = 6.0d;
        boolean hitPlayers = true;
        boolean hitMobs = false;

        public forceField(UUID player) {
            this.player = player;
        }
    }
}

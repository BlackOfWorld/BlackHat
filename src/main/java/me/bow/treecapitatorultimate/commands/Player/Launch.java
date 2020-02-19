package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

@Command.Info(command = "launch", description = "Launches the player to the space", category = CommandCategory.Player, requiredArgs = 1)
public class Launch extends Command {
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        Player anotherPlayer = Bukkit.getPlayer(args.get(0));
        if (anotherPlayer == null) {
            Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
            return;
        }
        anotherPlayer.setVelocity(new Vector(0, 4000, 0));
        this.Reply(p, ChatColor.GREEN + "Yeeeeeee! Player launched!");
        this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " launched " + anotherPlayer.getDisplayName() + " into the sky!");
    }
}

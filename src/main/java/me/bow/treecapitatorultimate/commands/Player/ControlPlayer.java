package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.DoubleHashMap;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

@Command.Info(command = "control", description = "Control other players!", category = CommandCategory.Player)
public class ControlPlayer extends Command {
    private DoubleHashMap<Player, Player> controlList = new DoubleHashMap<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (controlList.containsKey(p)) {
            p.Reply(ChatColor.RED + "Control stopped!");
            controlList.remove(p);
            return;
        }
        if (args.size() < 1) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Not enough arguments!");
            return;
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (controlList.containsKey(e.getPlayer())) controlList.remove(e.getPlayer());
        if (controlList.containsValue(e.getPlayer())) controlList.removeValue(e.getPlayer());
    }
}

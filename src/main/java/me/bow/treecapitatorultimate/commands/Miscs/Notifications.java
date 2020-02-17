package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command.Info(command = "notifications", description = "Notifications when trusted people do something stupid", category = CommandCategory.Miscs)
public class Notifications extends Command {

    List<UUID> ignored = new ArrayList<UUID>();
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        //TODO: implement this
    }
}

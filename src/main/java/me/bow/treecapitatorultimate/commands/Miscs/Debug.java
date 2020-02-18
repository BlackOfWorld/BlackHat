package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "debug", description = "Debug", category = CommandCategory.Miscs)
public class Debug extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        p.sendMessage(Start.COMMAND_PREFIX + "Name: " + p.getName());
        p.sendMessage(Start.COMMAND_PREFIX + "Display name: " + p.getDisplayName());
        p.sendMessage(Start.COMMAND_PREFIX + "UUID: " + p.getUniqueId().toString() + " (Hash code: " + p.getUniqueId().hashCode() + ")");
        p.sendMessage(Start.COMMAND_PREFIX + "Health: " + p.getHealth());
        p.sendMessage(Start.COMMAND_PREFIX + "Health scale: " + p.getHealthScale());
        p.sendMessage(Start.COMMAND_PREFIX + "Locale: " + p.getLocale());
        p.sendMessage(Start.COMMAND_PREFIX + "Op: " + p.isOp());
        p.sendMessage(Start.COMMAND_PREFIX + "Dead: " + p.isDead());
        p.sendMessage(Start.COMMAND_PREFIX + "Gravity: " + p.hasGravity());
        p.sendMessage(Start.COMMAND_PREFIX + "Location: " + p.getLocation());
        p.sendMessage(Start.COMMAND_PREFIX + "Walk speed: " + p.getWalkSpeed());
        p.sendMessage(Start.COMMAND_PREFIX + "Fly speed: " + p.getFlySpeed());
        p.sendMessage(Start.COMMAND_PREFIX + "Address: " + p.getAddress());
    }
}

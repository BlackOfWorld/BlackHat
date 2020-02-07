package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Debug extends Command {
    public Debug() {
        super("debug", "Debug", CommandCategory.Miscs, 0);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        p.sendMessage(Start.Prefix + "Name: " + p.getName());
        p.sendMessage(Start.Prefix + "Display name: " + p.getDisplayName());
        p.sendMessage(Start.Prefix + "Health: " + p.getHealth());
        p.sendMessage(Start.Prefix + "Health scale: " + p.getHealthScale());
        p.sendMessage(Start.Prefix + "Locale: " + p.getLocale());
        p.sendMessage(Start.Prefix + "Op: " + p.isOp());
        p.sendMessage(Start.Prefix + "Dead: " + p.isDead());
        p.sendMessage(Start.Prefix + "Gravity: " + p.hasGravity());
        p.sendMessage(Start.Prefix + "Location: " + p.getLocation());
        p.sendMessage(Start.Prefix + "Walk speed: " + p.getWalkSpeed());
        p.sendMessage(Start.Prefix + "Fly speed: " + p.getFlySpeed());
        p.sendMessage(Start.Prefix + "Address: " + p.getAddress());
    }
}

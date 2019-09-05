package me.bow.treecapitatorultimate.command;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class Command implements CommandEvents {
    private final String command;
    private final String description;
    private final CommandCategory category;
    private final int requiredArgs;

    public Command(String command, String description, CommandCategory category) {
        this(command, description, category, 0);
    }

    public Command(String command, String description, CommandCategory category, int requiredArgs) {
        this.command = command;
        this.description = description;
        this.category = category;
        this.requiredArgs = requiredArgs;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public int getRequiredArgs() {
        return requiredArgs;
    }

    public abstract void onCommand(Player p, ArrayList<String> args);

}

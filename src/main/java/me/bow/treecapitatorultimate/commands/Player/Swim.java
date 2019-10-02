package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import static me.bow.treecapitatorultimate.Utils.CraftBukkitUtil.getEntityMetadata;

public class Swim extends Command {
    private ArrayList<UUID> players = new ArrayList<>();

    public Swim() {
        super("swim", "Player will have swim animation, even on land!", CommandCategory.Player, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() == 0) return;
                for (UUID i : players) {
                    Player p = Bukkit.getPlayer(i);
                    if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR)
                        return;
                    Material m = p.getLocation().getBlock().getType();
                    if (m == Material.WATER || m == Material.LAVA) return; //let's not reveal ourselves
                    p.setSwimming(true);
                    for (Player d : Bukkit.getOnlinePlayers()) {
                        DataWatcher data = ((CraftLivingEntity) d).getHandle().getDataWatcher();
                        data.set(DataWatcherRegistry.a.a(0), getEntityMetadata(false, false, true, true, false, false, false)); // 0x2 for sneak, 0x8 for hide arm, 0x10 for swimming
                        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(p.getEntityId(), data, false);
                        final Object nmsPlayer;
                        final Field playerConnectionField;
                        Object pConnection = null;
                        Method sendPacket = null;
                        Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
                        try {
                            nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
                            playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
                            pConnection = playerConnectionField.get(nmsPlayer);
                            sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            for (Player p2 : Bukkit.getOnlinePlayers()) {
                                if (sendPacket != null) {
                                    sendPacket.invoke(pConnection, packet);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskTimer(Start.Instance, 0, 5);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player is not online!");
                return;
            }
            if (!players.contains(anotherPlayer.getUniqueId())) {
                anotherPlayer.setSwimming(true);
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " is now swimming!");
                players.add(anotherPlayer.getUniqueId());
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " is now longer swimming!");
            players.remove(anotherPlayer.getUniqueId());
            anotherPlayer.setSwimming(false);
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }

    @Override
    public void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        if (!players.contains(e.getEntity().getUniqueId())) return;
        if (e.isSwimming()) return;
        e.setCancelled(true);
    }
}

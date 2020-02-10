package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command.Info(command = "criticals", description = "Makes your attack critical!", category = CommandCategory.Player)
public class Criticals extends Command {
    private final Class packetClass = ReflectionUtils.getMinecraftClass("Packet");
    private final ReflectionUtils.ConstructorInvoker packetPlayOutAnimation = ReflectionUtils.getConstructor("{nms}.PacketPlayOutAnimation", ReflectionUtils.getClassCached("{nms}.Entity"), int.class);
    private final List<UUID> players = new ArrayList<UUID>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "Criticals disabled!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "Criticals enabled!");
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player whoHit = (Player) e.getDamager();

        e.setDamage(e.getDamage() * 1.5F);
        try {
            Object nmsPlayer = whoHit.getClass().getMethod("getHandle").invoke(whoHit);
            Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            Object pConnection = playerConnectionField.get(nmsPlayer);
            Method sendPacket = pConnection.getClass().getMethod("sendPacket", packetClass);
            sendPacket.invoke(pConnection, packetPlayOutAnimation.invoke(nmsPlayer, 5));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

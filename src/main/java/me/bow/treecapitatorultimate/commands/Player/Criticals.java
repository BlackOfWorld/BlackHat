package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

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
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Criticals enabled!");
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && players.contains(damager.getUniqueId())) {
            e.setCancelled(true);
            return;
        } else if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!players.contains(damager.getUniqueId())) return;
        if (damager.getFallDistance() > 0.0F &&
                !damager.isOnGround() &&
                !damager.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                !damager.isInsideVehicle() &&
                damager.getLocation().getBlock().getType() != Material.LADDER &&
                damager.getLocation().getBlock().getType() != Material.VINE &&
                !damager.isSprinting()) return;
        Entity damagee = e.getEntity();
        e.setDamage(e.getDamage() * 1.5F);
        try {
            Object nmsEntity = damagee.getClass().getMethod("getHandle").invoke(damagee);
            PacketSender.Instance.sendPacket(damager, Packet.createFromNMSPacket(packetPlayOutAnimation.invoke(nmsEntity, 4)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

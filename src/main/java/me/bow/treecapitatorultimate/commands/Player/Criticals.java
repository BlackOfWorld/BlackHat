package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Command.Info(command = "criticals", description = "Makes your attack critical!", category = CommandCategory.Player)
public class Criticals extends Command {
    private static final Class<?> PACKET_CLASS =
            Objects.requireNonNull(ReflectionUtils.getClass("{nms}.PacketPlayOutWorldParticles"));
    private final ReflectionUtils.ConstructorInvoker packetPlayOutAnimation = ReflectionUtils.getConstructor("{nms}.PacketPlayOutAnimation", ReflectionUtils.getClassCached("{nms}.Entity"), int.class);
    private final List<UUID> players = new ArrayList<UUID>();
    private Method getParticleName;
    private Field particleParamField;

    public Criticals() {
        PacketInjector.addPacketListener(this);
        if (ReflectionUtils.versionIsNewerOrEqualAs(1, 13, 0)) {
            for (Field field : PACKET_CLASS.getDeclaredFields()) {
                if (!field.getType().getSimpleName().equals("ParticleParam")) continue;
                this.particleParamField = field;
                this.particleParamField.setAccessible(true);
                break;
            }
            Class<?> particleParamClass = ReflectionUtils.getClass("{nms}.ParticleParam");
            for (Method method : particleParamClass.getMethods()) {
                if (method.getReturnType() != String.class) continue;
                getParticleName = method;
                break;
            }
        } else {
            for (Field field : PACKET_CLASS.getDeclaredFields()) {
                if (field.getType().getSimpleName().equals("EnumParticle")) {
                    this.particleParamField = field;
                    this.particleParamField.setAccessible(true);
                }
            }
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Criticals disabled!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Criticals enabled!");
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

    @Override
    public void onPacketSend(PacketEvent e) {
        if (!players.contains(e.getPlayer().getUniqueId())) return;
        Packet p = e.getPacket();
        if (!p.getNMSPacket().getClass().getSimpleName().equals("PacketPlayOutWorldParticles")) return;
        Object a = p.getNMSPacket();
        try {
            String particleName;
            if (getParticleName == null) {
                particleName = ((Enum<?>) particleParamField.get(a)).name();
            } else {
                particleName = (String) getParticleName.invoke(particleParamField.get(a));
            }
            if (!particleName.toLowerCase().contains("sweep")) return;
            e.setCancelled(true);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }

    }
}

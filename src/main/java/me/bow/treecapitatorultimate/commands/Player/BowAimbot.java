package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class BowAimbot extends Command {
    private ArrayList<UUID> players = new ArrayList<>();

    public BowAimbot() {
        super("bowaimbot", "Bow aimbot, yes that's it", CommandCategory.Player, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer bow aimbotting!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now bow aimbotting!");
        }
    }

    @Override
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getProjectile() instanceof Arrow)) return;
        Player p = (Player) e.getEntity();
        if (!players.contains(p.getUniqueId())) return;
        double minAng = 6.28;
        Entity minEntity = null;
        for (Entity entity : p.getNearbyEntities(64.0, 64.0, 64.0)) {
            if (!p.hasLineOfSight(entity) || !(entity instanceof LivingEntity)) continue;
            Vector to = entity.getLocation().toVector().clone().subtract(p.getLocation().toVector());
            double angle = e.getProjectile().getVelocity().angle(to);
            if (!(angle < minAng)) continue;
            minAng = angle;
            minEntity = entity;
        }
        Arrow arrow = (Arrow) e.getProjectile();
        LivingEntity target = (LivingEntity) minEntity;
        if (minEntity != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Vector newVel;
                    double speed = arrow.getVelocity().length();
                    if (arrow.isOnGround() || arrow.isDead() || target.isDead()) {
                        this.cancel();
                        return;
                    }
                    Vector to = target.getLocation().clone().add(new Vector(0.0, 0.5, 0.0)).subtract(arrow.getLocation()).toVector();
                    Vector dirVel = arrow.getVelocity().clone().normalize();
                    Vector dirTarget = to.clone().normalize();
                    double ang = dirVel.angle(dirTarget);
                    double speed_ = 0.9 * speed + 0.13999999999999999;
                    if (target instanceof Player && arrow.getLocation().distance(target.getLocation()) < 8.0 && ((Player) target).isBlocking()) {
                        speed_ = speed * 0.6;
                    }
                    if (ang < 0.12) {
                        newVel = dirVel.clone().multiply(speed_);
                    } else {
                        Vector newDir = dirVel.clone().multiply((ang - 0.12) / ang).add(dirTarget.clone().multiply(0.12 / ang));
                        newDir.normalize();
                        newVel = newDir.clone().multiply(speed_);
                    }
                    arrow.setVelocity(newVel.add(new Vector(0.0, 0.03, 0.0)));
                    arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 0);
                }
            }.runTaskTimer(Start.Instance, 1L, 1L);
        }
    }
}

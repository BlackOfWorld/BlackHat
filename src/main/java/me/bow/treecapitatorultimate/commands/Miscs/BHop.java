package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.bow.treecapitatorultimate.commands.Miscs.BHop.bhopSpeed.Hop;

public class BHop extends Command {
    private final double jumpHeight = 0;//0.41999998688697815D;
    @SuppressWarnings("unchecked")
    private HashMap<UUID, bhopInfo> players = new HashMap();

    public BHop() {
        super("bhop", "Bunnyhop like it's easter!", CommandCategory.Miscs, 0);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.get(p.getUniqueId()) != null) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer BHopping!");
            return;
        }
        if (args.size() == 0) return;
        String type = args.get(0);
        if (!type.equalsIgnoreCase("yport") && !type.equalsIgnoreCase("hop") && !type.equalsIgnoreCase("smallhop"))
            return;
        bhopSpeed speed = null;
        if (type.equalsIgnoreCase("yPort"))
            speed = bhopSpeed.yPort;
        if (type.equalsIgnoreCase("smallhop"))
            speed = bhopSpeed.smallHop;
        if (type.equalsIgnoreCase("hop"))
            speed = Hop;
        bhopInfo info = new bhopInfo();
        info.hopSpeed = speed;
        players.put(p.getUniqueId(), info);
        p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now BHopping!");
    }

    @Override
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;
        Player p = e.getPlayer();
        bhopInfo speed = players.get(p.getUniqueId());
        if (speed == null) return;
        speed.enabled = !speed.enabled;
        if (!speed.enabled) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "BHop disabled! Sneak again to enable it again.");
        } else {
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "BHop enabled! Sneak again to disable it again.");
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        bhopInfo speed = players.get(p.getUniqueId());
        if (speed == null) return;
        if (!speed.enabled) return;
        //noinspection ConstantConditions
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        Material m = p.getLocation().getBlock().getType();
        if (m == Material.LAVA || m == Material.WATER) return;
        Vector velocity = p.getLocation().getDirection();
        switch (speed.hopSpeed) {
            case Hop:
                if (!p.isOnGround()) return;
                velocity.setY(0.41999998688697815D); // jump
                break;
            case yPort:
                if (p.isOnGround())
                    velocity.setY(0.41999998688697815D); // jump
                else
                    velocity.setY(-1);
                break;
            case smallHop:
                if (!p.isOnGround()) return;
                velocity.setY(0.25D); // jump
                break;
        }
        p.setVelocity(velocity);
        p.setSprinting(true);
    }

    enum bhopSpeed {
        yPort,
        Hop,
        smallHop
    }

    class bhopInfo {
        boolean enabled;
        bhopSpeed hopSpeed;

        bhopInfo() {
            enabled = true;
            hopSpeed = Hop;
        }
    }
}

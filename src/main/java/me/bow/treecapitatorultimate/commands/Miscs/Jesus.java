package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "jesus", description = "Walk on water! UwU", category = CommandCategory.Miscs)
public class Jesus extends Command {
    private ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer have jesus!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now jesus!");
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        Block liquidBlock = p.getLocation().subtract(0,0.00001, 0).getBlock();
        Boolean blockBelowLiquid = liquidBlock.isLiquid();
        if(p.hasGravity() && !blockBelowLiquid) return;
        p.setGravity(!blockBelowLiquid);
        if(!blockBelowLiquid) return;
        p.setVelocity(new Vector(0, 0.001, 0));
        /*boolean blockNext = false;
        for (int x = -1; x < 2; x++) {
            Location location = e.getTo();
            if (blockNext) break;
            for (int z = -1; z < 2; z++) {
                if (!location.add(x, location.getY(), z).getBlock().isLiquid()) continue;
                p.setVelocity(new Vector(0, 0.01, 0));
                blockNext = true;
                break;
            }
        }*/
    }
}

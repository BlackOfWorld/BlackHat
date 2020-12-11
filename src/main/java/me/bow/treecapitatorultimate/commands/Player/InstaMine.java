package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "instamine", description = "Mines blocks instantly... yeah", category = CommandCategory.Player)
public class InstaMine extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You are now now exhausted to break blocks fast");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "You gathered your left over strength and breaking blocks fast!");
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK) || !players.contains(p.getUniqueId())) return;
        e.setCancelled(true);
        Block b = e.getClickedBlock();
        if(b.getType() == Material.BEDROCK) return;
        b.breakNaturally(p.getInventory().getItemInMainHand());
    }
}
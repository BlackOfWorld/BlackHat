package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static me.bow.treecapitatorultimate.Utils.MathUtils.generateNumber;

public class LagPlayer extends Command {
    private ArrayList<UUID> players = new ArrayList<>();
    private Random rnd = new Random();

    public LagPlayer() {
        super("lagplayer", "HELP MY BLOCKS ARENT BREAKING HALP (fake lag)", CommandCategory.Player, 1);
    }

    @Override
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (p.getGameMode() == GameMode.CREATIVE && p.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("sword"))
            return;
        int randomTick = generateNumber(10, 25);
        if (p.getGameMode() == GameMode.CREATIVE) {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> e.getBlock().setType(Material.AIR), randomTick);
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> e.getBlock().breakNaturally(item), randomTick);
        }
        e.setCancelled(true);
    }

    @Override
    public void onPlayerBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        BlockData block = e.getBlock().getBlockData();
        Runnable runnable = () -> e.getBlockPlaced().setBlockData(block);
        int randomTick = generateNumber(10, 25);
        if (p.getGameMode() == GameMode.CREATIVE) {
            Bukkit.getScheduler().runTaskLater(Start.Instance, runnable, randomTick);
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, runnable, randomTick);
        }
        e.setCancelled(true);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        if (rnd.nextInt(100) > 10) return;
        Location from = e.getFrom();
        if (from.getBlock().equals(Objects.requireNonNull(e.getTo()).getBlock())) return;
        Bukkit.getScheduler().runTaskLater(Start.Instance, () -> p.teleport(from), 10);
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

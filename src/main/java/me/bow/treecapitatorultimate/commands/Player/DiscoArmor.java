package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class DiscoArmor extends Command {
    Random r = new Random();
    ArrayList<UUID> players = new ArrayList<>();

    public DiscoArmor() {
        super("discoarmor", "Hey, it's disco party time!", CommandCategory.Player, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() == 0) return;
                Color c = Color.fromRGB(r.nextInt(0xFFFFFF));
                for (UUID i : players) {
                    Player p = Bukkit.getPlayer(i);
                    if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR) return;
                    ItemStack[] armor = new ItemStack[]{getColorArmor(Material.LEATHER_BOOTS, c), getColorArmor(Material.LEATHER_LEGGINGS, c), getColorArmor(Material.LEATHER_CHESTPLATE, c), getColorArmor(Material.LEATHER_HELMET, c)};
                    p.getInventory().setArmorContents(armor);
                }
            }
        }.runTaskTimerAsynchronously(Start.Instance, 0, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() >= 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.ErrorString(p, "Player is not online!");


                    return;
                }
                if (!players.contains(anotherPlayer.getUniqueId())) {
                    p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " now has disco armor!");
                    players.add(anotherPlayer.getUniqueId());
                    return;
                }
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " now longer has disco armor!");
                players.remove(anotherPlayer.getUniqueId());
            } catch (Exception e) {
                Start.ErrorException(p, e);
            }
            return;
        }
        if (!players.contains(p.getUniqueId())) {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You now have disco armor!");
        } else {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You now longer have disco armor!");
        }
    }

    private ItemStack getColorArmor(Material m, Color c) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId());
    }
}

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
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Command.Info(command = "discoarmor", description = "Hey, it's disco party time!", category = CommandCategory.Player)
public class DiscoArmor extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();
    Random r = new Random();
    private int angle = 0;

    public DiscoArmor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() == 0) return;
                //Color c = Color.fromRGB(r.nextInt(0xFFFFFF));
                Color c = ToHSV(angle);
                angle += 10;
                if (angle >= 360) angle = 0;
                for (UUID i : players) {
                    Player p = Bukkit.getPlayer(i);
                    if ((p == null || !p.isOnline()) || p.getGameMode() == GameMode.SPECTATOR) return;
                    ItemStack[] armor = new ItemStack[]{getColorArmor(Material.LEATHER_BOOTS, c), getColorArmor(Material.LEATHER_LEGGINGS, c), getColorArmor(Material.LEATHER_CHESTPLATE, c), getColorArmor(Material.LEATHER_HELMET, c)};
                    p.getInventory().setArmorContents(armor);
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0, 1);
    }

    private Color ToHSV(double deg) {
        double rad = Math.PI / 180d * deg;
        double third = Math.PI / 3d;

        int r = (int) (Math.sin(rad) * 127 + 128);
        int g = (int) (Math.sin(rad + 2 * third) * 127 + 128);
        int b = (int) (Math.sin(rad + 4 * third) * 127 + 128);
        return Color.fromRGB(r, g, b);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.size() >= 1) {
            try {
                Player anotherPlayer = Bukkit.getPlayer(args.get(0));
                if (anotherPlayer == null) {
                    Start.Error(p, "Player is not online!");


                    return;
                }
                if (!players.contains(anotherPlayer.getUniqueId())) {
                    p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.GREEN + " now has disco armor!");
                    players.add(anotherPlayer.getUniqueId());
                    return;
                }
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + anotherPlayer.getName() + ChatColor.RED + " now longer has disco armor!");
                players.remove(anotherPlayer.getUniqueId());
            } catch (Exception e) {
                Start.Error(p, e);
            }
            return;
        }
        if (!players.contains(p.getUniqueId())) {
            players.add(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "You now have disco armor!");
        } else {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You now longer have disco armor!");
        }
    }

    private ItemStack getColorArmor(Material m, Color c) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        Objects.requireNonNull(meta).setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId());
    }
}

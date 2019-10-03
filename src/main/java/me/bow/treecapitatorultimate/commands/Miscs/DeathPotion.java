package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class DeathPotion extends Command {
    public DeathPotion() {
        super("deathpotion", "Kill people in survival and in creative too", CommandCategory.Miscs);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
        PotionEffect heal = new PotionEffect(PotionEffectType.HEAL, 2000, 125);
        PotionEffect harm = new PotionEffect(PotionEffectType.HARM, 2000, 125);
        assert potionmeta != null;
        potionmeta.addCustomEffect(heal, true);
        potionmeta.addCustomEffect(harm, true);
        potionmeta.setDisplayName("§6Splash Potion of §4§lDEATH");
        potion.setItemMeta(potionmeta);
        p.getInventory().addItem(potion);
    }
}

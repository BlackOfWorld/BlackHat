package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

@Command.Info(command = "enchant", description = "Something like Essentials's enchant", category = CommandCategory.Miscs, requiredArgs = 2)
public class Enchant extends Command {

    /**
     * @noinspection ConstantConditions
     */
    private void addEnchantment(final CommandSender sender, ItemStack stack, final Enchantment enchantment, final int level) {
        if (enchantment == null) {
            Start.Error(sender, "Enchantment cannot be null!");
        }
        try {
            if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                if (level == 0) meta.removeStoredEnchant(enchantment);
                else meta.addStoredEnchant(enchantment, level, true);
                stack.setItemMeta(meta);
            } else {
                if (level == 0) stack.removeEnchantment(enchantment);
                else stack.addUnsafeEnchantment(enchantment, level);
            }
        } catch (Exception ex) {
            Start.Error(sender, ex);
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        ItemStack stack = p.getInventory().getItemInMainHand();
        ItemMeta metaStack = stack.getItemMeta();
        int level;
        try {
            level = Integer.parseInt(args.get(1));
        } catch (Exception e) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Level cannot be string!");
            return;
        }


        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args.get(0).toLowerCase()));
        if (enchantment == null) {
            //noinspection deprecation
            enchantment = Enchantment.getByName(args.get(0).toUpperCase());
        }
        if (enchantment == null) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "That enchantment doesn't exist!");
            return;
        }
        addEnchantment(p, stack, enchantment, level);
        p.getInventory().setItemInMainHand(stack);
        p.updateInventory();
        p.sendMessage(level == 0 ? Start.COMMAND_PREFIX + ChatColor.GREEN + "Enchantment removed!" : Start.COMMAND_PREFIX + ChatColor.GREEN + "Enchantment added!");
    }
}

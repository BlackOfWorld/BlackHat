package me.bow.treecapitatorultimate.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class TreeDestroy implements Listener {
    @EventHandler
    public void breakingBlock(final BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (!this.isLog(e.getBlock().getType())) {
            return;
        }
        if (!this.isAxe(item)) {
            return;
        }
        if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        this.breakBlock(e.getBlock(), e.getPlayer());
    }

    private void breakBlock(final Block block, final Player p) {
        block.breakNaturally();
        ItemStack item = p.getInventory().getItemInMainHand();
        final Location top = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY() + 1, block.getLocation().getBlockZ());
        final Location left = new Location(block.getWorld(), block.getLocation().getBlockX() + 1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        final Location right = new Location(block.getWorld(), block.getLocation().getBlockX() - 1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        final Location z1 = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 1);
        final Location z2 = new Location(block.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() - 1);
        final Block blockLeft = left.getBlock();
        final Block blockAbove = top.getBlock();
        final Block blockRight = right.getBlock();
        final Block blockZ1 = z1.getBlock();
        final Block blockZ2 = z2.getBlock();
        if (this.isLog(blockAbove.getType()) || this.isLog(blockLeft.getType()) || this.isLog(blockRight.getType()) || this.isLog(blockZ1.getType()) || this.isLog(blockZ2.getType()))
            this.breakBlock(blockAbove, p);
        if (this.isLog(blockLeft.getType())) {
            checkAndBreak(p, blockLeft, blockRight, blockZ1, blockZ2);
        } else if (this.isLog(blockRight.getType())) {
            checkAndBreak(p, blockRight, blockLeft, blockZ1, blockZ2);
        } else if (this.isLog(blockZ1.getType())) {
            checkAndBreak(p, blockZ1, blockZ2, blockLeft, blockRight);
        } else if (this.isLog(blockZ2.getType())) {
            this.breakBlock(blockZ2, p);
            if (this.isLog(blockZ1.getType())) {
                this.breakBlock(blockZ1, p);
                if (this.isLog(blockLeft.getType())) {
                    this.breakBlock(blockLeft, p);
                }
                if (this.isLog(blockRight.getType())) {
                    this.breakBlock(blockRight, p);
                }
            }
        }
        short loss = getDurability(item);
        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasEnchants()) {
            loss += (short) ((int) (loss * 1.0D / (item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) + 1.0D)));
        }
        this.setDurability(item, loss);
        if (this.getDurability(item) > item.getType().getMaxDurability()) {
            p.getInventory().remove(p.getInventory().getItemInMainHand());
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }

    private void checkAndBreak(Player p, Block blockLeft, Block blockRight, Block blockZ1, Block blockZ2) {
        this.breakBlock(blockLeft, p);
        if (this.isLog(blockRight.getType())) {
            this.breakBlock(blockRight, p);
        }
        if (this.isLog(blockZ1.getType())) {
            this.breakBlock(blockZ1, p);
        }
        if (this.isLog(blockZ2.getType())) {
            this.breakBlock(blockZ2, p);
        }
    }

    private boolean isLog(final Material material) {
        return material.toString().toLowerCase().contains("log");
    }

    private boolean isAxe(final ItemStack item) {
        return item.getType().toString().toLowerCase().contains("axe");
    }

    private void setDurability(ItemStack item, final short durability) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ((Damageable) meta).setDamage(durability);
            item.setItemMeta(meta);
        }
    }

    private short getDurability(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta == null) ? 0 : (short) ((Damageable) meta).getDamage();
    }
}

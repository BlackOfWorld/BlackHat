package me.bow.treecapitatorultimate.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class TreeDestroy implements Listener {
    @EventHandler
    public void breakingBlock(final BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }

        if (!this.isLog(e.getBlock().getType())) {
            return;
        }
        if (!this.isAxe(e.getPlayer().getInventory().getItemInMainHand())) {
            return;
        }
        if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        this.breakBlock(e.getBlock(), e.getPlayer());
    }

    public void breakBlock(final Block block, final Player p) {
        block.breakNaturally();
        final Location top = new Location(block.getWorld(), (double) block.getLocation().getBlockX(), (double) (block.getLocation().getBlockY() + 1), (double) block.getLocation().getBlockZ());
        final Location left = new Location(block.getWorld(), (double) (block.getLocation().getBlockX() + 1), (double) block.getLocation().getBlockY(), (double) block.getLocation().getBlockZ());
        final Location right = new Location(block.getWorld(), (double) (block.getLocation().getBlockX() - 1), (double) block.getLocation().getBlockY(), (double) block.getLocation().getBlockZ());
        final Location z1 = new Location(block.getWorld(), (double) block.getLocation().getBlockX(), (double) block.getLocation().getBlockY(), (double) (block.getLocation().getBlockZ() + 1));
        final Location z2 = new Location(block.getWorld(), (double) block.getLocation().getBlockX(), (double) block.getLocation().getBlockY(), (double) (block.getLocation().getBlockZ() - 1));
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
        this.setDurability(p.getInventory().getItemInMainHand(), (short) (this.getDurability(p.getInventory().getItemInMainHand()) + 1));
        if (this.getDurability(p.getInventory().getItemInMainHand()) > p.getInventory().getItemInMainHand().getType().getMaxDurability()) {
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

    public boolean isLog(final Material material) {
        return material.equals(Material.ACACIA_LOG) || material.equals(Material.BIRCH_LOG) || material.equals(Material.DARK_OAK_LOG) || material.equals(Material.JUNGLE_LOG) || material.equals(Material.OAK_LOG) || material.equals(Material.SPRUCE_LOG);
    }

    public boolean isAxe(final ItemStack item) {
        return item.getType().equals(Material.WOODEN_AXE) || item.getType().equals(Material.STONE_AXE) || item.getType().equals(Material.IRON_AXE) || item.getType().equals(Material.GOLDEN_AXE) || item.getType().equals(Material.DIAMOND_AXE);
    }

    public void setDurability(ItemStack item, final short durability) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ((Damageable) meta).setDamage(durability);
            item.setItemMeta(meta);
        }
    }

    public short getDurability(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta == null) ? 0 : (short) ((Damageable) meta).getDamage();
    }
}

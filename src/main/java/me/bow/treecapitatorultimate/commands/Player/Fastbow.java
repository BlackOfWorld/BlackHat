package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "fastbow", description = "ACCURATE SPEEDY ARROW SHOOTER", category = CommandCategory.Player)
public class Fastbow extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();
    private static final int crossbowExist = (1 << 0);
    private static final int tridentExist = (1 << 1);
    private int isExist = 0;

    public Fastbow() {
        for (Material m : Material.values()) {
            if (!m.name().toLowerCase().contains("trident")) {
                isExist |= crossbowExist;
            } else if (!m.name().toLowerCase().contains("crossbow")) {
                isExist |= tridentExist;
            }
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You are no longer fastbowing!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "You are now fastbowing!");
        }
    }

    private void shootArrowNMS(Player p) {
        try {
            final Object nmsPlayer = CraftBukkitUtil.getNmsPlayer(p);
            final Object nmsWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
            final Object itemStack = CraftBukkitUtil.getNmsItemStack(p.getInventory().getItemInMainHand());
            Method d = ReflectionUtils.getMethodCached(itemStack.getClass(), "a", ReflectionUtils.getClassCached("{nms}.World"), ReflectionUtils.getClass("{nms}.EntityLiving"), int.class);
            d.invoke(itemStack, nmsWorld, nmsPlayer, 0);
        } catch (Exception e) {
            Start.Error(p, e);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final Action action = e.getAction();
        if (!players.contains(player.getUniqueId()) ||
                (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK))
                || (player.getInventory().getItemInMainHand().getType() != Material.BOW
                && ((isExist & tridentExist) == tridentExist && player.getInventory().getItemInMainHand().getType() != Material.TRIDENT)
                && ((isExist & crossbowExist) == crossbowExist && player.getInventory().getItemInMainHand().getType() != Material.CROSSBOW))) {
            return;
        }
        shootArrowNMS(player);

        // to force client to "redraw" bow
        int slot = player.getInventory().getHeldItemSlot();
        int nextSlot = slot == 0 ? slot + 1 : slot - 1;
        Bukkit.getScheduler().runTask(this.plugin, () -> player.getInventory().setHeldItemSlot(nextSlot));
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.getInventory().setHeldItemSlot(slot), 2);
    }

    @Override
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e) {
        if((isExist & crossbowExist) != crossbowExist) return;
        Player p = e.getPlayer();
        if (!players.contains(p.getUniqueId())) return;
        ItemStack crossbow = p.getInventory().getItem(e.getNewSlot());
        if(crossbow.getType() != Material.CROSSBOW) return;
        CrossbowMeta meta = (CrossbowMeta)crossbow.getItemMeta();
        meta.addChargedProjectile(new ItemStack(Material.ARROW, 1));
        try {
            ReflectionUtils.getFieldCached(meta.getClass(), "charged").set(meta, true);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        crossbow.setItemMeta(meta);
    }
}

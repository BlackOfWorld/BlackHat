package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "fastbow", description = "ACCURATE SPEEDY ARROW SHOOTER", category = CommandCategory.Player)
public class Fastbow extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer fastbowing!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now fastbowing!");
        }
    }

    private void shootArrow(Player p) {
        p.launchProjectile(Arrow.class).setCritical(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 10.0f, 1.0f);
    }

    private void shootArrowNMS(Player p) {
        //WorldServer world = ((CraftWorld) p.getWorld()).getHandle();
        try {
            final Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
            final Object nmsWorld = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
            Class<?> craftItemStack = ReflectionUtils.getClassCached("{obc}.inventory.CraftItemStack");
            Object itemStack = ReflectionUtils.getMethodCached(craftItemStack, "asNMSCopy").invoke(craftItemStack, p.getInventory().getItemInMainHand());
            Method d = ReflectionUtils.getMethodCached(itemStack.getClass(), "a", ReflectionUtils.getClassCached("{nms}.World"), ReflectionUtils.getClass("{nms}.EntityLiving"), int.class);
            //World world, EntityLiving entityliving, int i
            d.invoke(itemStack, nmsWorld, nmsPlayer, 0);
        } catch (Exception e) {
            Start.ErrorException(p, e);
            //this.players.remove(p.getUniqueId());
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final Action action = e.getAction();
        //Bukkit.broadcastMessage(Start.Prefix+ChatColor.RED+"[DEBUG] Player interact action: "+action.toString());
        if (!players.contains(player.getUniqueId()) || (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK)) || (player.getInventory().getItemInMainHand().getType() != Material.BOW && player.getInventory().getItemInMainHand().getType() != Material.TRIDENT)) {
            return;
        }
        //e.setUseItemInHand(Event.Result.DENY);
        //e.setUseInteractedBlock(Event.Result.DENY);
        shootArrowNMS(player);
        int slot = player.getInventory().getHeldItemSlot();
        int nextSlot = slot == 0 ? slot + 1 : slot - 1;
        //Bukkit.broadcastMessage(Start.Prefix + ChatColor.AQUA + "[DEBUG] Current slot: " + slot + " | next slot: " + (slot == 0 ? slot + 1 : slot - 1));
        Bukkit.getScheduler().runTask(Start.Instance, () -> player.getInventory().setHeldItemSlot(nextSlot));
        Bukkit.getScheduler().runTaskLater(Start.Instance, () -> player.getInventory().setHeldItemSlot(slot), 2);
    }
}

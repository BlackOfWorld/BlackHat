package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class More extends Command {
    public More() {
        super("more", "Gimme more items please", CommandCategory.Miscs);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        final ItemStack stack = p.getInventory().getItemInMainHand();
        stack.setAmount(127);
        p.updateInventory();
    }
}

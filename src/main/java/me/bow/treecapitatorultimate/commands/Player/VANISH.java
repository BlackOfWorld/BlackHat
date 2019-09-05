package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class VANISH extends Command {
    ArrayList<Player> invisPlayers = new ArrayList<Player>();

    public VANISH() {
        super("vanish", "Y-you saw nothing!", CommandCategory.Player);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (invisPlayers.contains(p)) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer in vanish.");
            invisPlayers.remove(p);
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == p) continue;
                e.showPlayer(Start.Instance, p);
            }
        } else {
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now in vanish.");
            invisPlayers.add(p);
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == p) continue;
                try {
                    e.hidePlayer(Start.Instance, p);
                } catch (Exception f) {
                    Start.ErrorException(p, f);
                }
            }
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        for (Player p : invisPlayers)
            e.getPlayer().hidePlayer(Start.Instance, p);
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        for (Player p : invisPlayers)
            if (this.invisPlayers.contains(e.getPlayer())) this.invisPlayers.remove(e.getPlayer());
            else e.getPlayer().showPlayer(Start.Instance, p);
    }
}

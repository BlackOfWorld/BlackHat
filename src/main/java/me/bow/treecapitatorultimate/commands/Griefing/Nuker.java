package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.Blocks;
import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.ChunkSection;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

//TODO: optimize nuker, can learn from https://www.spigotmc.org/threads/best-method-for-placing-a-large-amount-of-blocks.299034/page-2
public class Nuker extends Command implements Listener {
    private ArrayList<nukerInfo> griefPlayers = new ArrayList<>();
    private int buildLimit;

    public Nuker() {
        super("nuker", "Breaks blocks around you", CommandCategory.Griefing, 0);
        buildLimit = Start.GetServer().propertyManager.getProperties().maxBuildHeight - 1;
    }

    private void setBlockSuperFast(Block b) {
        net.minecraft.server.v1_14_R1.Chunk chunk = ((CraftChunk) b.getChunk()).getHandle();

        try {
            Field f = chunk.getClass().getDeclaredField("sections");
            f.setAccessible(true);
            ChunkSection[] sections = (ChunkSection[]) f.get(chunk);
            ChunkSection chunksection = sections[b.getY() >> 4];

            if (chunksection == null) {
                chunksection = sections[b.getY() >> 4] = new ChunkSection(b.getY() >> 4 << 4);
            }

            chunksection.setType(b.getX() & 15, b.getY() & 15, b.getZ() & 15, Blocks.AIR.getBlockData());
            //chunksection.b(b.getX() & 15, b.getY() & 15, b.getZ() & 15, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        int index = isPart(p.getUniqueId());
        if (index != -1) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Nuker disabled!");
            griefPlayers.remove(index);
        } else {
            if (args.size() == 0) {
                p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments! (" + args.size() + " out of " + this.getRequiredArgs() + ")");
                return;
            }
            int range;
            try {
                range = Integer.parseInt(args.get(0));
            } catch (Exception e2) {
                Start.ErrorException(p, e2);
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "Nuker enabled!");
            nukerInfo info = new nukerInfo(p.getUniqueId(), range);
            griefPlayers.add(info);
        }
    }


    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        int index = isPart(e.getPlayer().getUniqueId());
        if (index == -1) return;
        nukerInfo info = griefPlayers.get(index);
        Player p = e.getPlayer();
        Location l = p.getLocation();
        for (double x = l.getBlockX() - info.range; x <= l.getBlockX() + info.range; x++)
            for (double y = l.getBlockY() - info.range; y <= l.getBlockY() + info.range; y++)
                for (double z = l.getBlockZ() - info.range; z <= l.getBlockZ() + info.range; z++) {
                    if (y < 0) {
                        y = 0;
                    }
                    if (y > buildLimit) {
                        y = buildLimit;
                    }
                    Location lc = new Location(p.getWorld(), x, y, z);
                    try {
                        if (lc.getBlock().getType().equals(Material.AIR)) continue;
                        if (!lc.getChunk().isLoaded()) continue;
                        if (info.range <= 5)
                            lc.getBlock().setType(Material.AIR);
                        else
                            setBlockSuperFast(lc.getBlock());
                    } catch (Exception e2) {
                        Start.ErrorException(p, e2);

                        //noinspection SuspiciousMethodCalls
                        griefPlayers.remove(info.player);
                    }
                }
        RefreshChunks(p, info.range);
    }


    private void RefreshChunks(Player p, int radius) {
        Location l = p.getLocation();
        for (int zPos = (int) l.getZ() + radius; zPos > l.getZ() - radius; zPos -= 16) {
            for (int xPos = (int) l.getX() + radius; xPos > l.getX() - radius; xPos -= 16) {
                Location chunkLoc = new Location(p.getWorld(), xPos, 0, zPos);
                Chunk chunk = ((CraftChunk) p.getWorld().getChunkAt(chunkLoc)).getHandle();
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 65535));
            }
        }
    }

    private int isPart(UUID uuid) {
        for (int i = 0; i < griefPlayers.size(); i++)
            if (griefPlayers.get(i).player.equals(uuid))
                return i;
        return -1;
    }

    final class nukerInfo {
        UUID player;
        int range;

        nukerInfo(UUID player, int range) {
            this.player = player;
            this.range = range;
        }
    }
}

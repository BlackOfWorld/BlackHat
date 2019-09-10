package me.bow.treecapitatorultimate.commands.Griefing;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

//TODO: optimize nuker, can learn from https://www.spigotmc.org/threads/best-method-for-placing-a-large-amount-of-blocks.299034/page-2
public class Nuker extends Command implements Listener {
    ArrayList<nukerInfo> griefPlayers = new ArrayList<>();
    int buildLimit;
    public Nuker() {
        super("nuker", "Breaks blocks around you", CommandCategory.Griefing, 0);
        buildLimit = Start.GetServer().propertyManager.getProperties().maxBuildHeight;
    }

    final static void setBlockFast(Block block, IBlockData b) {
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        Chunk c = ((CraftChunk)block.getChunk()).getHandle();
        c.setType(bp, b, true);
    }

    public static void setBlockSuperFast(Block b) {
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
                    if(y < 0) {
                        y = 0;
                    }
                    if(y > buildLimit) {
                        y = buildLimit;
                    }
                    Location lc = new Location(p.getWorld(), x, y, z);
                    try {
                        if (lc.getBlock().getType().equals(Material.AIR)) continue;
                        if(!lc.getChunk().isLoaded()) continue;
                        setBlockSuperFast(lc.getBlock());
                        RefreshChunks2(p, info.range);
//                        for (Entity ef : lc.getChunk().getEntities()) {
//                            if (ef instanceof Player) {
//                                Player ppe = (Player) ef;
//
//                            }
//                        }
                        //lc.getBlock().setType(Material.AIR);
                    } catch (Exception e2) {
                        Start.ErrorException(p, e2);
                        griefPlayers.remove(info.player);
                    }
                }
    }

    void RefreshChunks2(Player p, int radius) {
        Location l = p.getLocation();
        BlockData air = Material.AIR.createBlockData();
        int radiusChunks = radius / 16;
        int maxRenderdistance = Start.GetServer().propertyManager.getProperties().viewDistance;
        if(radiusChunks > maxRenderdistance)
            radius = maxRenderdistance * 16;
        for (double x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++)
            for (double y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++)
                for (double z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++)
                    ((Player) p).sendBlockChange(new Location(p.getWorld(), x, y, z), air);

        for (Entity ps : p.getNearbyEntities(radius, radius, radius)) {
            if (!(ps instanceof Player)) {
                continue;
            }
            Player ppe = (Player) ps;
            for (double x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++)
                for (double y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++)
                    for (double z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++)
                        ((Player) ps).sendBlockChange(new Location(ppe.getWorld(), x, y, z), air);
        }
    }

    void RefreshChunks(Player p, int radius) {
        Location l = p.getLocation();
        for (int zPos = (int) l.getZ() + radius; zPos > l.getZ() - radius; zPos -= 16) {
            for (int xPos = (int) l.getX() + radius; xPos > l.getX() - radius; xPos -= 16) {
                Location chunkLoc = new Location(p.getWorld(), xPos, 0, zPos);
                Chunk chunk = ((CraftChunk) p.getWorld().getChunkAt(chunkLoc)).getHandle();
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 65535));
            }
        }
    }
    final int isPart(UUID uuid) {
        for (int i = 0; i < griefPlayers.size(); i++)
            if (griefPlayers.get(i).player.equals(uuid))
                return i;
        return -1;
    }
    final class nukerInfo {
        UUID player;
        int range;

        public nukerInfo(UUID player, int range) {
            this.player = player;
            this.range = range;
        }
    }
}

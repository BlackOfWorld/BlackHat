package me.bow.treecapitatorultimate.commands.Griefing;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

//TODO: optimize nuker, can learn from https://www.spigotmc.org/threads/best-method-for-placing-a-large-amount-of-blocks.299034/page-2
@Command.Info(command = "nuker", description = "Breaks blocks around you", category = CommandCategory.Griefing)
public class Nuker extends Command {
    private Map<UUID, Integer> griefPlayers = new HashMap<>();
    private int buildLimit;
    private Queue<Block> blockQueue = new ArrayDeque<>();

    public Nuker() {
        try {
            Object dedicatedServerProperties = Start.getDedicatedServerPropertiesInstance();
            buildLimit = (int)ReflectionUtils.getField(dedicatedServerProperties.getClass(), "maxBuildHeight").get(dedicatedServerProperties);
        } catch (Exception e) {
            Bukkit.broadcastMessage(e.getMessage());
        }
        //buildLimit = propertyManager.getProperties().maxBuildHeight - 1;
    }

    private static void setBlockInNativeWorld(Block block, boolean applyPhysics) {
        net.minecraft.server.v1_15_R1.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData ibd = Blocks.AIR.getBlockData();
        nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        Object index = griefPlayers.get(p.getUniqueId());
        if (index != null) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "Nuker disabled!");
            griefPlayers.remove(p.getUniqueId());
        } else {
            if (args.size() == 0) {
                p.sendMessage(Start.Prefix + ChatColor.RED + "Not enough arguments! (" + args.size() + " out of 1)");
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
            griefPlayers.put(p.getUniqueId(), range);
        }
    }

    @Override
    public void onServerTick() {
        int blockCount = 0;
        Block block;
        while (blockQueue.size() != 0) {
            block = blockQueue.poll();
            if (blockCount++ >= 1500000) {
                Bukkit.broadcastMessage(Integer.toString(blockQueue.size()));
                return;
            }
            setBlockInNativeWorld(block, false);
            Bukkit.broadcastMessage(String.valueOf(block.getY()));
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Object index = griefPlayers.get(e.getPlayer().getUniqueId());
        if (index == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(Start.Instance, () -> {
            try {
                int range = (int) index;
                Player p = e.getPlayer();
                Location l = p.getLocation();
                for (double x = l.getBlockX() - range; x <= l.getBlockX() + range; x++)
                    for (double y = l.getBlockY() - range; y <= l.getBlockY() + range; y++)
                        for (double z = l.getBlockZ() - range; z <= l.getBlockZ() + range; z++) {
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
                                //Bukkit.getScheduler().runTask(Start.Instance, () -> );
                                blockQueue.add(lc.getBlock()); // list is full
                                //setBlockSuperFast(lc.getBlock()); // let's do it rn
                            } catch (Exception e2) {
                                Start.ErrorException(p, e2);

                                griefPlayers.remove(e.getPlayer().getUniqueId());
                                return;
                            }
                        }
                RefreshChunks(p, range);
            } catch (Exception ex) {
                Bukkit.broadcastMessage(ex.getMessage());
            }
        });
    }

    private Collection<org.bukkit.Chunk> getChunksAroundPlayer(Player player, int range) {
        int[] offset = Ints.toArray(ContiguousSet.create(Range.closed(-range, range), DiscreteDomain.integers()).asList());


        World world = player.getWorld();
        int baseX = player.getLocation().getChunk().getX();
        int baseZ = player.getLocation().getChunk().getZ();

        Collection<org.bukkit.Chunk> chunksAroundPlayer = new HashSet<>();
        for (int x : offset) {
            for (int z : offset) {
                org.bukkit.Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(chunk);
            }
        }
        return chunksAroundPlayer;
    }

    private void RefreshChunks(Player p, int radius) {
        int view = Bukkit.getServer().getViewDistance();
        if (radius < 16)
            radius = 16;
        int chunksRadius = radius / 16;
        if (chunksRadius > view)
            chunksRadius = view;
        Collection<org.bukkit.Chunk> chunks = getChunksAroundPlayer(p, chunksRadius);
        int ticks = 0;
        for (org.bukkit.Chunk chunk : chunks) {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> {
                Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(nmsChunk, 65535));
                //noinspection deprecation
                p.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
            }, ticks++);
        }
    }
}
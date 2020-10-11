package me.bow.treecapitatorultimate.Utils;

import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import net.minecraft.server.v1_15_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_15_R1.PacketPlayOutMultiBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockUtil {
    public static Location getNearestAirBlock(Location l, int maxRadius) {
        if (l == null) {
            throw new IllegalArgumentException("l cannot be null.");
        }
        if (maxRadius < 1) {
            maxRadius = 1;
        }
        if (!Bukkit.isPrimaryThread() && !l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
            return LocationUtils.toBlockLocation(l);
        }
        l = LocationUtils.toBlockLocation(l);
        if (l.getBlock().getType() == Material.AIR) {
            return l;
        }
        Location closest = null;
        for (int x = maxRadius * -1; x <= maxRadius; ++x) {
            for (int z = maxRadius * -1; z <= maxRadius; ++z) {
                for (int y = maxRadius * -1; y <= maxRadius; ++y) {
                    final Location l2 = l.clone().add(x, y, z);
                    if (l2.getBlock().getType() == Material.AIR && (closest == null || l2.distanceSquared(l) < closest.distanceSquared(l))) {
                        closest = l2;
                    }
                }
            }
        }
        return (closest != null) ? closest : l;
    }

    public static List<Block> getBlocks(Location center, int xRadius, int yRadius, int zRadius) {
        if (center == null) {
            throw new IllegalArgumentException("center cannot be null.");
        }

        int minX = center.getBlockX() - xRadius;
        int maxX = center.getBlockX() + xRadius;
        int minY = center.getBlockY() - yRadius;
        int maxY = center.getBlockY() + yRadius;
        int minZ = center.getBlockZ() - zRadius;
        int maxZ = center.getBlockZ() + zRadius;

        Location currentLocation = new Location(center.getWorld(), 0.0d, 0.0d, 0.0d);
        List<Block> blocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            currentLocation.setX(x);
            for (int z = minZ; z <= maxZ; z++) {
                currentLocation.setZ(z);
                for (int y = minY; y <= maxY; y++) {
                    currentLocation.setY(y);
                    blocks.add(currentLocation.getBlock());
                }
            }
        }

        return blocks;
    }

    public static void sendBlocksChange(Player p, Chunk chunk, Tuple<Location, BlockData>... blocks) {
        PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange();
        Class<?> packetClass = packet.getClass();
        PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] info = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            Location location = blocks[i].a();
            int x = location.getBlockX() & 15;
            int z = location.getBlockZ() & 15;

            info[i] = packet.new MultiBlockChangeInfo((short) (x << 12 | z << 8 | location.getBlockY()), ((CraftBlockData) blocks[i].b()).getState());
        }
        try {
            Field field = packetClass.getDeclaredField("a");
            field.setAccessible(true);
            field.set(packet, new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
            field = packetClass.getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, info);
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(packet));
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Block getHighestSolidBlock(Location l) {
        if (l == null) {
            throw new IllegalArgumentException("l cannot be null.");
        }

        // We don't want to modify the original Location
        l = l.clone();
        // getType is a bit expensive
        Material type = l.getBlock().getType();

        if (!type.isSolid()) {
            // The block isn't solid, so we scan downwards to find the last non-solid block
            // Stop at 0 so we don't get stuck in an infinite loop
            while (l.getY() > 0 && !type.isSolid()) {
                // Apparently adding negatives is faster than subtracting (citation needed)
                l.add(0.0d, -1.0d, 0.0d);
                type = l.getBlock().getType();
            }
            // We don't care if 0 is the "highest" solid block because technically that's correct
            return l.getBlock();
        }

        // The block is solid, so we need to scan upwards to find the first non-solid block
        while (l.getY() < l.getWorld().getMaxHeight() && type.isSolid()) {
            l.add(0.0d, 1.0d, 0.0d);
            type = l.getBlock().getType();
        }
        // We don't care if maxHeight is the "highest" solid block because technically that's correct
        // If the block isn't solid, subtract 1 from it
        return type.isSolid() ? l.getBlock() : l.add(0.0d, -1.0d, 0.0d).getBlock();
    }

    public static Set<Block> getHalfCircleAround(Location loc, double radius, int numPoints, int maxHeight) {
        Set<Block> retVal = new HashSet<>();
        double piSlice = Math.PI / numPoints;

        double angle = loc.getYaw();

        while (angle < 0.0d) {
            angle += 360.0d;
        }
        while (angle > 360.0d) {
            angle -= 360.0d;
        }

        angle = angle * Math.PI / 180.0d;

        for (int i = 0; i < numPoints; i++) {
            double newAngle = angle + piSlice * i;
            Block b = getHighestSolidBlock(new Location(loc.getWorld(), loc.getX() + radius * Math.cos(newAngle), loc.getY(), loc.getZ() + radius * Math.sin(newAngle)));
            if (Math.abs(b.getLocation().getY() - loc.getY()) <= maxHeight) {
                retVal.add(b);
            }
        }

        return retVal;
    }
}

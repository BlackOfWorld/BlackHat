package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.*;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.*;

@Command.Info(command = "lsd", description = "LSD for everyone!", category = CommandCategory.Player)
public class LSD extends Command {

    private final int radius = 16;
    ArrayList<UUID> players = new ArrayList<>();
    short tick = 0;
    private BlockData[] wools;
    private int renderDistance = Bukkit.getViewDistance();

    public LSD() {
        List<BlockData> data = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.name().toLowerCase().contains("wool")) continue;
            data.add(m.createBlockData());
        }
        wools = data.stream().toArray(n -> new BlockData[n]);
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
                CraftBukkitUtil.refreshPlayer(p);
                anotherPlayer.resetPlayerTime();
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.RED + " is now longer on LSD!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " disabled LSD on " + anotherPlayer.getDisplayName() + "!");
            } else {
                players.add(anotherPlayer.getUniqueId());
                Object gameStateChange = ReflectionUtils.getConstructorCached(ReflectionUtils.getClassCached("{nms}.PacketPlayOutGameStateChange"), int.class, float.class).invoke(7, 15);
                PacketSender.Instance.sendPacket(anotherPlayer, Packet.createFromNMSPacket(gameStateChange));
                p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GOLD + anotherPlayer.getName() + ChatColor.GREEN + " is now on LSD!");
                this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " enabled LSD on " + anotherPlayer.getDisplayName() + "!");
            }
        } catch (Exception e) {
            Start.Error(p, e);
        }
    }

    @Override
    public void onServerTick() {
        tick++;
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            if (tick >= 5) {
                p.setPlayerTime(MathUtils.generateNumber(23999), false);
            }
            /*final Set<Location> locations = new HashSet<>();
            locations.addAll(this.getFilledCircle(p.getLocation(), radius, false));
            if (locations.size() <= radius * 2) {
                locations.clear();
                locations.addAll(this.getFilledCircle(p.getLocation(), radius, true));
            }
            HashMap<Chunk, List<Tuple<Location, BlockData>>> chunks = new HashMap<>();
            for (final Location l : locations) {
                Tuple<Location, BlockData> t = new Tuple<>(l, wools[MathUtils.generateNumber(wools.length - 1)]);
                if (chunks.containsKey(l.getChunk())) {
                    List<Tuple<Location, BlockData>> blocks = new LinkedList<>(chunks.get(l.getChunk()));
                    chunks.remove(l.getChunk());
                    blocks.add(t);
                    chunks.put(l.getChunk(), blocks);
                } else {
                    chunks.put(l.getChunk(), Collections.singletonList(t));
                }
            }
            for (final Chunk chunk : chunks.keySet()) {
                BlockUtil.sendBlocksChange(p, chunk, (chunks.get(chunk)).toArray(new Tuple[0]));
            }*/
        }
        if (tick >= 5) tick = 0;
    }


    //huge optimization for client <3
    private boolean isBlockVisible(Block block) {
        if (!block.getType().isSolid()) return false; // we don't care about liquids
        BlockFace[] checks = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
        for (BlockFace check : checks) {
            Block relBlock = block.getRelative(check);
            if (!relBlock.getType().isAir() && relBlock.getType().isOccluding()) continue;
            return true;
        }
        return false;
    }

    /*private Set<Location> getFilledCircle(Location l, final int radius, final boolean useGround) {
        final HashSet<Location> retVal = new HashSet<>();
        if (useGround) {
            l = BlockUtil.getHighestSolidBlock(l).getLocation();
            retVal.add(l);
        } else if (l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
            l = LocationUtils.toBlockLocation(l);
            if (l.getBlock().getType().isSolid()) {
                retVal.add(l);
            }
        }
        for (int i = 1; i <= radius; ++i) {
            final Location[] locs = LocationUtils.getCircleAround(l, i, radius * 8);
            for (int j = 0; j < locs.length; ++j) {
                for (int k = -radius; k < radius; ++k) {
                    final Location t = locs[j].clone().add(0.0, k, 0.0);
                    if (!t.getWorld().isChunkLoaded(t.getBlockX() >> 4, t.getBlockZ() >> 4) || !isBlockVisible(t.getBlock()))
                        continue;
                    retVal.add(t);
                }
            }
        }
        return retVal;
    }
*/
}

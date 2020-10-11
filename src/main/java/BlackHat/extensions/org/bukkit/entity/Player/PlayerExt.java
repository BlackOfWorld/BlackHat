package BlackHat.extensions.org.bukkit.entity.Player;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.BlockUtil;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.Utils.Tuple;
import me.bow.treecapitatorultimate.commands.Miscs.Notifications;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Extension
public class PlayerExt {
    public static int getPing(@This Player thiz) {
        try {
            return (int) ReflectionUtils.getFieldCached(ReflectionUtils.getClassCached("{nms}.EntityPlayer"), "ping").get(CraftBukkitUtil.getNmsPlayer(thiz));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void Reply(@This Player player, String message) {
        player.sendMessage(Start.COMMAND_PREFIX + message.replaceAll("\n", "\n" + Start.COMMAND_PREFIX));
    }
    public static void Notify(@This Player p, String msg) {
        Notifications.NotifyLocal(p, msg);
    }

    public static void sendPacket(@This Player thiz, Packet... packets) throws InvocationTargetException, IllegalAccessException {
        PacketSender.Instance.sendPacket(thiz, packets);
    }

    public static Object getNmsPlayer(@This Player p) {
        return CraftBukkitUtil.getNmsPlayer(p);
    }

    public static Object getObcPlayer(@This Player p) {
        return CraftBukkitUtil.getObcPlayer(p);
    }

    public static void sendBlocksChange(@This Player p, Chunk chunk, Tuple<Location, BlockData>... blocks) {
        BlockUtil.sendBlocksChange(p, chunk, blocks);
    }
}
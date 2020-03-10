package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.NPC.NPC;
import me.bow.treecapitatorultimate.Utils.Packet.PacketEvent;
import me.bow.treecapitatorultimate.Utils.Packet.PacketInjector;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

@Command.Info(command = "debug", description = "Debug", category = CommandCategory.Miscs)
public class Debug extends Command {

    HashSet<NPC> NPCs = new HashSet<>();

    public Debug() {
        PacketInjector.addPacketListener(this);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        p.sendMessage(Start.COMMAND_PREFIX + "Name: " + p.getName());
        p.sendMessage(Start.COMMAND_PREFIX + "Display name: " + p.getDisplayName());
        p.sendMessage(Start.COMMAND_PREFIX + "UUID: " + p.getUniqueId().toString() + " (Hash code: " + p.getUniqueId().hashCode() + ")");
        p.sendMessage(Start.COMMAND_PREFIX + "Health: " + p.getHealth());
        p.sendMessage(Start.COMMAND_PREFIX + "Health scale: " + p.getHealthScale());
        p.sendMessage(Start.COMMAND_PREFIX + "Locale: " + p.getLocale());
        p.sendMessage(Start.COMMAND_PREFIX + "Op: " + p.isOp());
        p.sendMessage(Start.COMMAND_PREFIX + "Dead: " + p.isDead());
        p.sendMessage(Start.COMMAND_PREFIX + "Gravity: " + p.hasGravity());
        p.sendMessage(Start.COMMAND_PREFIX + "Location: " + p.getLocation());
        p.sendMessage(Start.COMMAND_PREFIX + "Walk speed: " + p.getWalkSpeed());
        p.sendMessage(Start.COMMAND_PREFIX + "Fly speed: " + p.getFlySpeed());
        p.sendMessage(Start.COMMAND_PREFIX + "Address: " + p.getAddress());
        if (args.size() != 0) {
            switch (args.get(0)) {
                case "lookat":
                    for (NPC npc: NPCs) {
                        npc.LookAt(p.getLocation().getYaw(), p.getLocation().getPitch());
                    }
                    break;
                case "despawn":
                    for (NPC npc: NPCs) {
                        npc.Despawn();
                    }
                    break;
                case "hide":
                    for (NPC npc: NPCs) {
                        npc.Hide(p);
                    }
                    break;
                case "show":
                    for (NPC npc: NPCs) {
                        npc.Show(p);
                    }
                    break;
            }
        } else {
            NPC npc = new NPC(p.getLocation(), "Attacker", "", "");
            try {
                npc.Spawn();
                npc.Show(p);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            NPCs.add(npc);

        }
    }

    @Override
    public void onPacketReceived(PacketEvent packetEvent) {
        onPacketSend(packetEvent);
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        //String msg = (packetEvent.getDirection() == PacketEvent.ConnectionDirection.TO_CLIENT ? (ChatColor.RED + "[Server -> Client] ") : (ChatColor.GREEN + "[Client -> Server] ")) + packetEvent.getPacket().getNMSPacket();
        //Start.LOGGER.log(Level.INFO, msg);
    }
}

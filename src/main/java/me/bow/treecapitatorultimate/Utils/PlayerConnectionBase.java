/*package me.bow.treecapitatorultimate.Utils;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumChatVisibility;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.NetworkManager;
import net.minecraft.server.v1_15_R1.PacketPlayInChat;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerConnectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class PlayerConnectionBase extends PlayerConnection {
    Method handleCommand;
    private PlayerConnection oldConnection = null;
    private MinecraftServer server;
    private Player p;
    public PlayerConnectionBase(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
        server = minecraftserver;
        handleCommand = ReflectionUtils.getMethod(PlayerConnection.class, "handleCommand", String.class);
        p = getPlayer();
    }

    public PlayerConnection getOldConnection() {
        return this.oldConnection;
    }

    public void setOldConnection(Object connection) {
        this.oldConnection = connection;
    }

    @Override
    public void a(PacketPlayInChat packetplayinchat) {
        boolean isSync = packetplayinchat.b().startsWith("/");
        if (!isSync) {
            super.a(packetplayinchat);
            return;
        }
        PlayerConnectionUtils.ensureMainThread(packetplayinchat, this, this.player.getWorldServer());

            if (!this.player.dead && this.player.getChatFlags() != EnumChatVisibility.HIDDEN) {
                this.player.resetIdleTimer();
                String s = packetplayinchat.b();
                s = StringUtils.normalizeSpace(s);
                try {
                    this.server.server.playerCommandState = true;
                    try {
                        if (!this.server.server.dispatchCommand(p, s.substring(1))) return;
                    } catch (CommandException var8) {
                        p.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
                        java.util.logging.Logger.getLogger(PlayerConnection.class.getName()).log(Level.SEVERE, null, var8);
                        return;
                    }
                } finally {
                    this.server.server.playerCommandState = false;
                }
        }
    }
}
*/
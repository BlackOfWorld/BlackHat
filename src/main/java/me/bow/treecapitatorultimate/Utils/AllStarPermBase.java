package me.bow.treecapitatorultimate.Utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllStarPermBase extends PermissibleBase {

    private static Field ATTACHMENTS_FIELD;

    static {
        try {
            ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            ATTACHMENTS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // if the permissible is currently active.
    private final AtomicBoolean active = new AtomicBoolean(false);
    // the player this permissible is injected into.
    private Player player;
    // the players previous permissible. (the one they had before this one was injected)
    private PermissibleBase oldPermissible = null;

    public AllStarPermBase(Player player) {
        super(player);
    }


    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return true;
    }

    @Override
    public void recalculatePermissions() {
        super.recalculatePermissions();
    }

    @Override
    public void clearPermissions() {
    }

    public Player getPlayer() {
        return this.player;
    }

    public PermissibleBase getOldPermissible() {
        return this.oldPermissible;
    }

    public void setOldPermissible(PermissibleBase oldPermissible) {
        this.oldPermissible = oldPermissible;
    }

    public AtomicBoolean getActive() {
        return this.active;
    }
}
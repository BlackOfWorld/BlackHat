package me.bow.treecapitatorultimate.Utils;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DummyPermissibleBase extends PermissibleBase {
    public static final DummyPermissibleBase INSTANCE = new DummyPermissibleBase();
    private static final Field ATTACHMENTS_FIELD;
    private static final Field PERMISSIONS_FIELD;

    static {
        try {
            ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            ATTACHMENTS_FIELD.setAccessible(true);
            PERMISSIONS_FIELD = PermissibleBase.class.getDeclaredField("permissions");
            PERMISSIONS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DummyPermissibleBase() {
        super(null);
        try {
            ATTACHMENTS_FIELD.set(this, new ArrayList<PermissionAttachment>() {
                @Override
                public boolean add(PermissionAttachment permissionAttachment) {
                    return true;
                }

                @Override
                public void add(int index, PermissionAttachment element) {
                }

                @Override
                public boolean addAll(Collection<? extends PermissionAttachment> c) {
                    return true;
                }

                @Override
                public boolean addAll(int index, Collection<? extends PermissionAttachment> c) {
                    return true;
                }
            });
        } catch (Exception e) {
            // ignore
        }
        try {
            PERMISSIONS_FIELD.set(this, new HashMap<String, PermissionAttachmentInfo>() {
                @Override
                public PermissionAttachmentInfo put(String key, PermissionAttachmentInfo value) {
                    return null;
                }

                @Override
                public void putAll(Map<? extends String, ? extends PermissionAttachmentInfo> m) {
                }

                @Override
                public PermissionAttachmentInfo putIfAbsent(String key, PermissionAttachmentInfo value) {
                    return null;
                }

                @Override
                public PermissionAttachmentInfo compute(String key, BiFunction<? super String, ? super PermissionAttachmentInfo, ? extends PermissionAttachmentInfo> remappingFunction) {
                    return null;
                }

                @Override
                public PermissionAttachmentInfo computeIfPresent(String key, BiFunction<? super String, ? super PermissionAttachmentInfo, ? extends PermissionAttachmentInfo> remappingFunction) {
                    return null;
                }

                @Override
                public PermissionAttachmentInfo computeIfAbsent(String key, Function<? super String, ? extends PermissionAttachmentInfo> mappingFunction) {
                    return null;
                }
            });
        } catch (Exception e) {
            // ignore
        }
    }

    public static void copyFields(PermissibleBase from, PermissibleBase to) {
        try {
            ATTACHMENTS_FIELD.set(to, ATTACHMENTS_FIELD.get(from));
        } catch (Exception e) {
            // ignore
        }
        try {
            PERMISSIONS_FIELD.set(to, PERMISSIONS_FIELD.get(from));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isPermissionSet(String name) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return false;
    }

    @Override
    public boolean hasPermission(String inName) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public void clearPermissions() {
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

}
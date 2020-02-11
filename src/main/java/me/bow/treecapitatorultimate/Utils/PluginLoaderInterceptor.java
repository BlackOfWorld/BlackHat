package me.bow.treecapitatorultimate.Utils;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public class PluginLoaderInterceptor implements PluginManager {
    SimplePluginManager manager;

    public PluginLoaderInterceptor(SimplePluginManager manager) {
        this.manager = manager;
    }

    @Override
    public void registerInterface(@NotNull Class<? extends PluginLoader> aClass) throws IllegalArgumentException {
        manager.registerInterface(aClass);
    }

    @Override
    public @Nullable Plugin getPlugin(@NotNull String s) {
        return manager.getPlugin(s);
    }

    @Override
    public @NotNull Plugin[] getPlugins() {
        return manager.getPlugins();
    }

    @Override
    public boolean isPluginEnabled(@NotNull String s) {
        return manager.isPluginEnabled(s);
    }

    @Override
    public boolean isPluginEnabled(@Nullable Plugin plugin) {
        return manager.isPluginEnabled(plugin);
    }

    @Override
    public @Nullable
    synchronized Plugin loadPlugin(@NotNull File file) throws InvalidPluginException, UnknownDependencyException {
        return manager.loadPlugin(file);
    }

    @Override
    public @NotNull Plugin[] loadPlugins(@NotNull File file) {
        return manager.loadPlugins(file);
    }

    @Override
    public void disablePlugins() {
        manager.disablePlugins();
    }

    @Override
    public void clearPlugins() {
        manager.clearPlugins();
    }

    @Override
    public void callEvent(@NotNull Event event) throws IllegalStateException {
        manager.callEvent(event);
    }

    @Override
    public void registerEvents(@NotNull Listener listener, @NotNull Plugin plugin) {
        manager.registerEvents(listener, plugin);
    }

    @Override
    public void registerEvent(@NotNull Class<? extends Event> aClass, @NotNull Listener listener, @NotNull EventPriority eventPriority, @NotNull EventExecutor eventExecutor, @NotNull Plugin plugin) {
        manager.registerEvent(aClass, listener, eventPriority, eventExecutor, plugin);
    }

    @Override
    public void registerEvent(@NotNull Class<? extends Event> aClass, @NotNull Listener listener, @NotNull EventPriority eventPriority, @NotNull EventExecutor eventExecutor, @NotNull Plugin plugin, boolean b) {
        manager.registerEvent(aClass, listener, eventPriority, eventExecutor, plugin, b);
    }

    @Override
    public void enablePlugin(@NotNull Plugin plugin) {
        manager.enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(@NotNull Plugin plugin) {
        manager.disablePlugin(plugin);
    }

    @Override
    public @Nullable Permission getPermission(@NotNull String s) {
        return manager.getPermission(s);
    }

    @Override
    public void addPermission(@NotNull Permission permission) {
        manager.addPermission(permission);
    }

    @Override
    public void removePermission(@NotNull Permission permission) {
        manager.removePermission(permission);
    }

    @Override
    public void removePermission(@NotNull String s) {
        manager.removePermission(s);
    }

    @Override
    public @NotNull Set<Permission> getDefaultPermissions(boolean b) {
        return manager.getDefaultPermissions(b);
    }

    @Override
    public void recalculatePermissionDefaults(@NotNull Permission permission) {
        manager.recalculatePermissionDefaults(permission);
    }

    @Override
    public void subscribeToPermission(@NotNull String s, @NotNull Permissible permissible) {
        manager.subscribeToPermission(s, permissible);
    }

    @Override
    public void unsubscribeFromPermission(@NotNull String s, @NotNull Permissible permissible) {
        manager.unsubscribeFromPermission(s, permissible);
    }

    @Override
    public @NotNull Set<Permissible> getPermissionSubscriptions(@NotNull String s) {
        return manager.getPermissionSubscriptions(s);
    }

    @Override
    public void subscribeToDefaultPerms(boolean b, @NotNull Permissible permissible) {
        manager.subscribeToDefaultPerms(b, permissible);
    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean b, @NotNull Permissible permissible) {
        manager.unsubscribeFromDefaultPerms(b, permissible);
    }

    @Override
    public @NotNull Set<Permissible> getDefaultPermSubscriptions(boolean b) {
        return manager.getDefaultPermSubscriptions(b);
    }

    @Override
    public @NotNull Set<Permission> getPermissions() {
        return manager.getPermissions();
    }

    @Override
    public boolean useTimings() {
        return manager.useTimings();
    }
}

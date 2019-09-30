package me.bow.treecapitatorultimate.command;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

public interface CommandEvents {
    default void onPlayerJoin(PlayerJoinEvent e) {
    }

    default void onPlayerLeave(PlayerQuitEvent e) {
    }

    default void onPlayerMove(PlayerMoveEvent e) {
    }

    default void onPlayerCommand(PlayerCommandPreprocessEvent e) {
    }

    default void onPlayerTab(PlayerCommandSendEvent e) {
    }

    default void onPlayerSneak(PlayerToggleSneakEvent e) {
    }

    default void onPlayerSprint(PlayerToggleSprintEvent e) {
    }

    default void onPlayerFly(PlayerToggleFlightEvent e) {
    }

    default void onPlayerPortal(PlayerPortalEvent e) {
    }

    default void onPlayerKick(PlayerKickEvent e) {
    }

    default void onServerCommand(ServerCommandEvent e) {
    }

    default void onTabControl(TabCompleteEvent e) {
    }

    default void onServerTick() {
    }

    default void onPlayerDeath(PlayerDeathEvent e) {
    }

    default void onPlayerBlockPlace(BlockPlaceEvent e) {
    }

    default void onPlayerBlockBreak(BlockBreakEvent e) {
    }

    default void onPlayerSwimToggle(EntityToggleSwimEvent e) {
    }

    default void onPlayerAdvancementGet(PlayerAdvancementDoneEvent e) {
    }

    default void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
    }

    default void onEntityTarget(EntityTargetEvent e) {
    }

    default void onPlayerInteract(PlayerInteractEvent e) {
    }

    default void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    }

    default void onEntityDeath(EntityDeathEvent e) {
    }

    default void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
    }

    default void onEntityPickupItem(EntityPickupItemEvent e) {
    }

    default void onEntityShootBow(EntityShootBowEvent e) {
    }
}

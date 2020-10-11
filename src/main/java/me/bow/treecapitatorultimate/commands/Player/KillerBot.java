package me.bow.treecapitatorultimate.commands.Player;

import me.bow.treecapitatorultimate.Utils.MathUtils;
import me.bow.treecapitatorultimate.Utils.NPC.NPC;
import me.bow.treecapitatorultimate.Utils.NPC.NPCManager;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Command.Info(command = "killerbots", description = "KILLER BOTS!!!1", category = CommandCategory.Player, requiredArgs = 1)
public class KillerBot extends Command {
    HashMap<UUID, List<NPC>> attackList = new HashMap<>();
    int rotate[] = {0,0,0,0};
    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        p.setGameMode(GameMode.SURVIVAL);
        int totalRotation = 0;
        int rotatePerBot = 360 / rotate.length;
        for(int i = 0; i < rotate.length; i++) {
            rotate[i] = totalRotation;
            totalRotation += rotatePerBot;
        }
        ArrayList<NPC> npcs = new ArrayList<>();
        for (int i = 0; i < rotate.length; i++) {
            NPC npc = NPCManager.createNPC(p, ChatColor.RED+"KillerBOT", "ewogICJ0aW1lc3RhbXAiIDogMTU5MzYzNDIzNDg4NywKICAicHJvZmlsZUlkIiA6ICJmNzhmYTg2N2VhMGY0NjljOWQ5ODRhYTk0YzkzZjVmYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcm9zR2FtZXJzNTIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBkNjYxYTZiMGUyZTE4MWNkODg2N2IxNDUzMjczYWIxNjNlMjZiY2ZjYmZkNWJhMWViOTU1NWQ0NGFjNzBlZSIKICAgIH0KICB9Cn0=", "wPFFCyyw0cncqLCKBDorPRDd94JRwFGTt7wdt+Q7c+Yi7nAIqIOoyHnBKH6kK7waw5+l0RdwtvYA1Gm6NrGqnRgUnZsnG5rsUdpI1sYnOEEXz5rdmkitcsXOLxud6cqa/rT95xEJYlCblw0CCZM6pyZZ1G3s+hjgC5PAAn/MZ0wfRSjq3Oy9KBZg/qsvP1QApxQWlYlnNRimewkrseZcm8NZVv9RS/A0FIkd3b69HYy03Arj5Aa6co43BJMEjBfVkxMt9GUo/NQD9LF0Nc7B8P/pcfFmS+sK+oYEZdf+ZXZhpNQlQL3FSJypyfXOf8I6MiayPl1l1MK1PJV/8qYPzsvAtkXMBqhnigJ0MEgJ7iFIP9sGahRHX/b0os5LDE1io+U8/uNgw3bu6nKmwvXifI2fa9yVX/WolMvfLgfGKf/7x2CI+t9smM/UX5AJ38FWD1MT2ezgWNr2KCJJwJntckWcbN058Ox83dA/jKENBlnNVxM669vP9GyPs4bfi1N9x3FOo2a1tvIhaq4QwcvMGQoPgi5ev3L2a0LfrEqWd9B5WoOc0yIB2nSG7mx48xrS13SdWqMCbmi2UMEvbo/niH5mTfWjdwejUQ5xOuYDcYjNb+A/7fsU3GZdgSFfimVh8onQdbdaFSKLmwQxWGwoxFCWNSxmrYtTxmCO5G7HBOM=");
            try {
                npc.Spawn();
                npc.Show(p);
                npcs.add(npc);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        attackList.put(p.getUniqueId(), npcs);
    }

    int tick = 0;


    @Override
    public void onServerTick() {
        if (tick++ != 2) return;
        for(int i = 0; i < rotate.length; i++) {
            rotate[i]++;
            if(rotate[i] > 360) rotate[i] = 0;
        }
        tick = 0;
        for (UUID player : attackList.keySet()) {
            Player p = Bukkit.getPlayer(player);
            List<NPC> npcs = attackList.get(player);
            for(int i= 0; i < npcs.size(); i++) {
                try {
                    npcs.get(i).Teleport(p.getLocation().add(Math.sin(rotate[i]) * 2, 0, Math.cos(rotate[i]) * 2));
                    npcs.get(i).LookAt(p.getLocation().add(0, MathUtils.generateNumber(0.4,0.75),0).toVector());
                    npcs.get(i).Attack(p);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            var damage = MathUtils.map(p.getAttribute(Attribute.GENERIC_ARMOR).getValue(), 0, 20, 2, 4,3);
            p.damage(damage * npcs.size());
        }
    }

    @Override
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!attackList.containsKey(p.getUniqueId())) return;
        List<NPC> npcs = attackList.get(p.getUniqueId());
        for(int i = 0; i < npcs.size(); i++) {
            try {
                npcs.get(i).Despawn();
            } catch (InvocationTargetException | IllegalAccessException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
        }
        attackList.remove(p.getUniqueId());
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!attackList.containsKey(p.getUniqueId())) return;
        attackList.remove(p.getUniqueId());
    }
}

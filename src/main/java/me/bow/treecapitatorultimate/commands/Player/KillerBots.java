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
public class KillerBots extends Command {
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
            NPC npc = NPCManager.createNPC(p, ChatColor.RED+"KillerBOT", "ewogICJ0aW1lc3RhbXAiIDogMTYwMjYxNzAwMDcyMiwKICAicHJvZmlsZUlkIiA6ICI0YjYzZjU5MzQyYzY0OWJlODQyMzU0NWVlMmY3NTE2ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5dW5hMjAwOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83NzZiMTk1MTlhNDZlZGM0YWUzOTA2NWY4ODljMTMyMjhmMGVmOTJlNjFiNGE1N2NlOWQ0ZWZhNTdlNWY2NjAwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0", "EOq8CYu4h614nlpqa/eGvF8Bp8yQTevA1z6kZS6kHtlKowpOVutPNIgJ5MBi4xvSlk0qUFwOzzkitTDCuNLt7fSHhJIRQW5EkA3UwHdE1P6g6ELzukPUu9TZcQJBb8l9YKhsqGiyxS1iaqpN4GA6fOpP+D1aOuDMuD493X/UV+2Lp7Vcc/68ryaz6nwigHFMklP7Az0zhGJ1/bC0AyPGUlJS2OInFXVauN/WAKf2L9wOFaGnTK7MTKf7/yxXBAzfz7Y27n2SR4brmT6hrh2Pirh2aDN6RUD5kWqN/lG5rk9J9HTcFoCgGsu0rj3oTvsIIKUlP/Eh5MaN4wY1fqzkgS5TMQOvD/+yzR1upSAhyQKREOEjIP0tTxA0kGK3XYznJwQLFGyEuK+mVilhGhEqbrWZ+/nvw3E9jE+Qxe6IusW+jAtJ/MlPh9FoQ48ADdOLv3nbQcIEpVrAiEos4nfyqakwtBfZtLaBN3zL0NEfAdHyuK4AQy/R5wHEw4vPh2aDR1Nzx7C6kaLbxlQocx3QcXKtnO59W7Mt2tNnK3OU3qE3tOtlr9kMckOuy9MmMarjQdsZ5G6Sl3QMeWg/YBcUAb7Uwl4ClpQsAw2XMVEentOqFyE9OIA2cGVI2ZBuiblXnGlk0z4GrlK0aaYrIguA/2jO5nuS8BbdZ9sOXXCf+Sw=");
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

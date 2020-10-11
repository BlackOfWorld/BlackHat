package me.bow.treecapitatorultimate.Utils.NPC;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NPCManager {
    public static NPC createNPC(Player player, String name) {
        Location loc = player.getLocation();
        return new NPC(loc, name, "", "");
    }
    public static NPC createNPC(Player player, String name, String texture) {
        Location loc = player.getLocation();
        return new NPC(loc, name, texture, "");
    }
    public static NPC createNPC(Player player, String name, String texture, String Signature) {
        Location loc = player.getLocation();
        return new NPC(loc, name, texture, Signature);
    }
}

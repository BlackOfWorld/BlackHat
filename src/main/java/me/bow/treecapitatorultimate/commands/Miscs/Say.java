package me.bow.treecapitatorultimate.commands.Miscs;

import me.bow.treecapitatorultimate.command.Command;
import me.bow.treecapitatorultimate.command.CommandCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Command.Info(command = "say", description = "Say something without triggering commands", category = CommandCategory.Player, requiredArgs = 1)
public class Say extends Command {

    @Override
    public void onCommand(Player p, ArrayList<String> args) {
        String msg = "";
        for (int i = 0; i < args.size(); i++) {
            msg += args.get(i) + " ";
        }
        msg = msg.replace("&", "§");
        p.chat(msg);
        /*for (Player pl : Bukkit.getOnlinePlayers()) {
            if(!pl.isOnline()) continue;
            //pl.sendRawMessage();
        }*/
    }
}

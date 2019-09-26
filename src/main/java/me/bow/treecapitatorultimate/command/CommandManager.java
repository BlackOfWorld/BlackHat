package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CommandManager {
    public ArrayList<Command> commandList = new ArrayList<>();

    public void Init() {
        org.slf4j.Logger bak = Reflections.log;
        Reflections.log = null;
        Reflections reflections = new Reflections("me.bow.treecapitatorultimate.command");
        Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> object : subTypes) {
            try {
                this.commandList.add(object.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                System.out.print(Arrays.toString(e.getStackTrace()));
            }
        }
        Bukkit.getScheduler().runTaskTimer(Start.Instance, new CommandRunnable(), 1L, 1L);
        Reflections.log = bak;
    }
}

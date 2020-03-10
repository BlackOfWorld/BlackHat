package me.bow.treecapitatorultimate.command;

import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

public class CommandManager {
    public ArrayList<Command> commandList = new ArrayList<>();

    public void Init() {
        org.slf4j.Logger bak = Reflections.log;
        Reflections.log = null;
        Reflections reflections = new Reflections(this.getClass().getPackage().getName());
        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(Command.Info.class);
        for (Class<?> object : subTypes) {
            try {
                this.commandList.add((Command) object.getDeclaredConstructor().newInstance());
            } catch (NoClassDefFoundError e) {
                Start.Instance.LOGGER.log(Level.SEVERE, e.getCause().toString());
            } catch (Exception e) {
                Start.Instance.LOGGER.log(Level.SEVERE, e.getCause().toString());
            }
        }
        try {
            Bukkit.getScheduler().runTaskTimer(Start.Instance, new CommandRunnable(), 1L, 1L);
        } catch (NoClassDefFoundError e) {
            //System.out.print(Arrays.toString(e.getStackTrace()));
        }
        Reflections.log = bak;
    }
}

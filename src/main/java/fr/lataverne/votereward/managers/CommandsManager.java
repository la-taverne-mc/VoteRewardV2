package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.commands.CompositeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandsManager {

    private final Map<String, CompositeCommand> commands = new HashMap<>();

    @SuppressWarnings("FieldNotUsedInToString")
    private SimpleCommandMap commandMap = null;

    public void registerCommand(@NotNull CompositeCommand command) {
        this.commands.put(command.getLabel(), command);

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());

            if (this.commandMap.register("votereward", command)) {
                VoteReward.sendMessageToConsole(
                        ChatColor.GREEN + "Registering the command \"" + command.getLabel() + "\" successful");
            } else {
                VoteReward.sendMessageToConsole(
                        ChatColor.RED + "Failed to register command \"" + command.getLabel() + "\"");
            }
        } catch (NoSuchFieldException e) {
            Bukkit.getLogger().severe("Unable to register a command because the Bukkit server doesn't have commandMap");
        } catch (IllegalAccessException e) {
            Bukkit.getLogger()
                  .severe("The commandMapField is enforcing Java language access control and the underlying field is inaccessible");
        }
    }

    @Override
    public String toString() {
        return "CommandsManager{" + "commands=" + this.commands + "}";
    }

    public void unregisterCommands() {
        if (this.commandMap == null) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) this.commandMap.getClass()
                                                                                       .getMethod("getKnownCommands")
                                                                                       .invoke(this.commandMap);
            knownCommands.values().removeIf(command -> this.commands.containsKey(command.getLabel()));

            this.commands.values().forEach(command -> command.unregister(this.commandMap));
            this.commands.clear();
        } catch (InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("Unable to get known commands");
        } catch (IllegalAccessException e) {
            Bukkit.getLogger()
                  .severe("The known commands can't be retrieved because the object is enforcing Java language access control and the underlying method is inaccessible");
        }
    }
}

package fr.lataverne.votereward.utils.commands;

import fr.lataverne.votereward.commands.common.ConfirmCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ConfirmableCommand extends CompositeCommand {

    private static final String CONFIRM_COMMAND_USAGE = "[confirm-command-usage]";

    protected ConfirmableCommand(@NotNull CompositeCommand parent, String label, String... aliases) {
        super(parent, label, aliases);
    }

    public abstract boolean toBeExecuted(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args);

    @Override
    protected final boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (!this.validate(sender, label, args)) {
            return true;
        }

        ConfirmCommand confirmCommand = this.getConfirmCommand();
        if (confirmCommand == null) {
            return this.toBeExecuted(sender, label, args);
        } else {
            confirmCommand.addCommandToBeConfirmed(this, sender, label, args);

            String message = this.plugin.getConfig()
                                        .getString("messages.confirm-command.confirm")
                                        .replace(CONFIRM_COMMAND_USAGE, confirmCommand.getUsage());

            sender.sendMessage(message);
            return true;
        }
    }

    protected abstract boolean validate(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args);
}

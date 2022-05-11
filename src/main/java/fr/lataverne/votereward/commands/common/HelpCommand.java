package fr.lataverne.votereward.commands.common;

import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class HelpCommand extends CompositeCommand {

    private static final String HELP_FOOTER = ChatColor.GREEN + "===================================";

    private static final String HELP_HEADER =
            ChatColor.GREEN + "============ " + ChatColor.DARK_GREEN + "VoteReward" + ChatColor.GREEN + " ============";

    private static final String PARENT_COMMAND_DESCRIPTION = "[parent-command-description]";

    private static final String PARENT_COMMAND_USAGE = "[parent-command-usage]";

    public HelpCommand(@NotNull CompositeCommand parent) {
        super(parent, "help");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (this.parent.isHidden()) {
            return true;
        }

        sender.sendMessage(" ");
        sender.sendMessage(HELP_HEADER);

        if (this.parent.hasSubCommands()) {
            Collection<CompositeCommand> subCommands = this.parent.getSubCommands();

            for (CompositeCommand subCommand : subCommands) {
                if (subCommand.canExecute(sender, false)) {
                    String subCommandUsage = subCommand.getUsage();
                    String subCommandParameters = subCommand.getParameters();
                    String subCommandDescription = subCommand.getDescription();

                    sender.sendMessage(subCommandParameters != null && !subCommandParameters.isEmpty()
                                       ? ChatColor.RED + subCommandUsage + " " + ChatColor.YELLOW +
                                         subCommandParameters + ChatColor.WHITE + " : " + ChatColor.GRAY +
                                         subCommandDescription
                                       : ChatColor.RED + subCommandUsage + ChatColor.WHITE + " : " + ChatColor.GRAY +
                                         subCommandDescription);
                }
            }
        } else {
            String commandUsage = this.parent.getUsage();
            String commandParameters = this.parent.getParameters();
            String commandDescription = this.parent.getDescription();

            String usage = this.plugin.getConfig().getString("messages.help.usage");
            String descriptionMessage = this.plugin.getConfig()
                                                   .getString("messages.help.description")
                                                   .replace(PARENT_COMMAND_DESCRIPTION, commandDescription);

            usage = commandParameters == null || commandParameters.isEmpty()
                    ? usage.replace(PARENT_COMMAND_USAGE, commandUsage)
                    : usage.replace(PARENT_COMMAND_USAGE, commandUsage + " " + commandParameters);

            sender.sendMessage(usage);
            sender.sendMessage(descriptionMessage);
        }

        sender.sendMessage(HELP_FOOTER);

        return true;
    }

    @Override
    public @NotNull String getUsage() {
        String usage = super.getUsage();
        return usage.replace(PARENT_COMMAND_USAGE, this.parent.getUsage());
    }

    @Override
    protected void setup() {
        this.inheritPermission();
        this.setHidden(true);
    }
}

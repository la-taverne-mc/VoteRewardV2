package fr.lataverne.votereward.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class HelpCommand extends CompositeCommand {

    private static final String PARENT_COMMAND_USAGE = "[parent-command-usage]";

    private static final String PARENT_COMMAND_DESCRIPTION = "[parent-command-description]";

    private static final String HELP_HEADER = ChatColor.GREEN + "============ " + ChatColor.DARK_GREEN + "VoteReward" + ChatColor.GREEN + " ============";

    private static final String HELP_FOOTER = ChatColor.GREEN + "===================================";

    public HelpCommand(@NotNull CompositeCommand parent) {
        super(parent, "help");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (this.parent.isHidden()) {
            return true;
        }

        sender.sendMessage(" ");
        sender.sendMessage(HelpCommand.HELP_HEADER);

        if (this.parent.hasSubCommands()) {
            Collection<CompositeCommand> subCommands = this.parent.getSubCommands();

            for (CompositeCommand subCommand : subCommands) {
                if (subCommand.canExecute(sender, false)) {
                    String subCommandUsage = subCommand.getUsage();
                    String subCommandParameters = subCommand.getParameters();
                    String subCommandDescription = subCommand.getDescription();

                    if (subCommandParameters != null && !subCommandParameters.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + subCommandUsage + " " + ChatColor.YELLOW + subCommandParameters + ChatColor.WHITE + " : " + ChatColor.GRAY + subCommandDescription);
                    } else {
                        sender.sendMessage(ChatColor.RED + subCommandUsage + ChatColor.WHITE + " : " + ChatColor.GRAY + subCommandDescription);
                    }
                }
            }
        } else {
            String commandUsage = this.getUsage();
            String commandParameters = this.getParameters();
            String commandDescription = this.getDescription();

            String usage = this.plugin.getConfig().getString("messages.help.usage");
            String descriptionMessage = this.plugin.getConfig().getString("messages.help.description").replace(HelpCommand.PARENT_COMMAND_DESCRIPTION, commandDescription);

            if (commandParameters == null || commandParameters.isEmpty()) {
                usage = usage.replace(HelpCommand.PARENT_COMMAND_USAGE, commandUsage);
            } else {
                usage = usage.replace(HelpCommand.PARENT_COMMAND_USAGE, commandUsage + " " + commandParameters);
            }

            sender.sendMessage(usage);
            sender.sendMessage(descriptionMessage);
        }

        sender.sendMessage(HelpCommand.HELP_FOOTER);

        return true;
    }

    @Override
    public @NotNull String getUsage() {
        String usage = super.getUsage();
        return usage.replace(HelpCommand.PARENT_COMMAND_USAGE, this.parent.getUsage());
    }

    @Override
    protected void setup() {
        this.inheritPermission();
        this.setHidden(true);
    }
}

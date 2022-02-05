package fr.lataverne.votereward.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class HelpCommand extends CompositeCommand {

    private static final String PARENT_COMMAND_USAGE = "[parent-command-usage]";

    private static final String HELP_HEADER = ChatColor.GREEN + "========== " + ChatColor.DARK_GREEN + "VoteReward" + ChatColor.GREEN + " ==========";

    private static final String HELP_FOOTER = ChatColor.GREEN + "================================";

    public HelpCommand(@NotNull CompositeCommand parent) {
        super(parent, "help");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        sender.sendMessage(" ");
        sender.sendMessage(HelpCommand.HELP_HEADER);

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
    }
}

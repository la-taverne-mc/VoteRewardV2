package fr.lataverne.votereward.commands.votereward.admin;

import fr.lataverne.votereward.commands.votereward.admin.rewardsgroups.ActivateCommand;
import fr.lataverne.votereward.commands.votereward.admin.rewardsgroups.CreateCommand;
import fr.lataverne.votereward.commands.votereward.admin.rewardsgroups.DeleteCommand;
import fr.lataverne.votereward.commands.votereward.admin.rewardsgroups.ListCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RewardsGroupsCommand extends CompositeCommand {

    public RewardsGroupsCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewardsgroups", "rgroups");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());

        if (cmdArgs.size() == 1) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.admin.rewards-group.unknown-rewards-group"));
        } else {
            this.misuseCommand(sender);
        }

        return true;
    }

    @Override
    protected void setup() {
        this.addChildren(new CreateCommand(this));
        this.addChildren(new DeleteCommand(this));
        this.addChildren(new ListCommand(this));
        this.addChildren(new ActivateCommand(this));
    }
}

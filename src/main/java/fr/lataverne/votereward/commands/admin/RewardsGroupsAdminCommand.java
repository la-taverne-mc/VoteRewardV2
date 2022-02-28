package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.ActivateRewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.CreateRewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.DeleteRewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.RewardsGroupListAdminCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RewardsGroupsAdminCommand extends CompositeCommand {

    public RewardsGroupsAdminCommand(@NotNull CompositeCommand parent) {
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
        this.setPermission("votereward.admin.rewardsgroups");

        new CreateRewardsGroupAdminCommand(this);
        new DeleteRewardsGroupAdminCommand(this);
        new RewardsGroupListAdminCommand(this);
        new ActivateRewardsGroupAdminCommand(this);
    }
}

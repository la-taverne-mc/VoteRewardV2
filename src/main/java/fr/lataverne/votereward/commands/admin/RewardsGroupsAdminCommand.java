package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.ActivateRewardsGroupAdminCommands;
import fr.lataverne.votereward.commands.admin.rewardsgroups.CreateRewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.RewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.RewardsGroupListAdminCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardsGroupsAdminCommand extends CompositeCommand {

    public RewardsGroupsAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewardsgroups", "rgroups");
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
                ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
                : new ArrayList<>();
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups");

        new CreateRewardsGroupAdminCommand(this);
        new RewardsGroupListAdminCommand(this);
        new ActivateRewardsGroupAdminCommands(this);
        new RewardsGroupAdminCommand(this);
    }
}

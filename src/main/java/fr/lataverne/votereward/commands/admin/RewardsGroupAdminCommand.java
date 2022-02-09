package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.ActivateRewardsGroupAdminCommands;
import fr.lataverne.votereward.commands.admin.rewardsgroups.CreateRewardsGroupAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.RewardsGroupListAdminCommand;
import org.jetbrains.annotations.NotNull;

public class RewardsGroupAdminCommand extends CompositeCommand {

    public RewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewardsgroups", "rgroups");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups");

        new CreateRewardsGroupAdminCommand(this);
        new RewardsGroupListAdminCommand(this);
        new ActivateRewardsGroupAdminCommands(this);
    }
}

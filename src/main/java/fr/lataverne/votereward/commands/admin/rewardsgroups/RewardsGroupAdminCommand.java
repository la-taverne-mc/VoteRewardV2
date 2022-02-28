package fr.lataverne.votereward.commands.admin.rewardsgroups;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.DynamicCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.rewardsgroup.AddRewardAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroups.rewardsgroup.SeeRewardsGroupAdminCommand;
import org.jetbrains.annotations.NotNull;

public class RewardsGroupAdminCommand extends CompositeCommand implements DynamicCommand {

    public RewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "<rewards-group-name>");
    }

    @Override
    public boolean isDynamicCommand(String label) {
        return this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream()
                          .anyMatch(subCmdLabel -> subCmdLabel.equalsIgnoreCase(label));
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups.<rewards-group-name>");

        new AddRewardAdminCommand(this);
        new SeeRewardsGroupAdminCommand(this);
    }
}
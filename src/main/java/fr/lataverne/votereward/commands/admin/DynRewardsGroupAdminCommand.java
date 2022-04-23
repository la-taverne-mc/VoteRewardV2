package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.DynamicCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroup.AddRewardAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroup.RewardAdminCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroup.SeeRewardsGroupAdminCommand;
import org.jetbrains.annotations.NotNull;

public class DynRewardsGroupAdminCommand extends CompositeCommand implements DynamicCommand {

    public DynRewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "<rewards-group-name>");
    }

    @Override
    public boolean isDynamicCommand(String label) {
        return this.plugin.getRewardsGroupManager()
                          .getRewardsGroups()
                          .keySet()
                          .stream()
                          .anyMatch(subCmdLabel -> subCmdLabel.equalsIgnoreCase(label));
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup");

        new AddRewardAdminCommand(this);
        new SeeRewardsGroupAdminCommand(this);
        new RewardAdminCommand(this);
    }
}

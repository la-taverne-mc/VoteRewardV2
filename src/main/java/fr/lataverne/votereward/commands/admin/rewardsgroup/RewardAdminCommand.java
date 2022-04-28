package fr.lataverne.votereward.commands.admin.rewardsgroup;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroup.reward.ModifyAdminCommand;
import org.jetbrains.annotations.NotNull;

public class RewardAdminCommand extends CompositeCommand {

    public RewardAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "reward");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup.<rewards-group-name>.reward");

        new ModifyAdminCommand(this);
    }
}

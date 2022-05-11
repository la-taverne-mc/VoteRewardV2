package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup;

import fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards.AddCommand;
import fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards.ModifyCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.jetbrains.annotations.NotNull;

public class RewardsCommand extends CompositeCommand {

    public RewardsCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewards");
    }

    @Override
    protected void setup() {
        this.addChildren(new ModifyCommand(this));
        this.addChildren(new AddCommand(this));
    }
}

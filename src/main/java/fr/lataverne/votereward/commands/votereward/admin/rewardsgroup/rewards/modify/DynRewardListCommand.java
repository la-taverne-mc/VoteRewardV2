package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards.modify;

import fr.lataverne.votereward.utils.commands.CompositeCommand;
import fr.lataverne.votereward.utils.commands.DynamicCommand;
import org.jetbrains.annotations.NotNull;

public class DynRewardListCommand extends DynamicCommand {

    public DynRewardListCommand(@NotNull CompositeCommand parent) {
        super(parent, "<reward-id>");
    }

    @Override
    public boolean isDynamicCommand(String label) {
        try {
            Double.parseDouble(label);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    protected void setup() {
        this.inheritPermission();

        this.addChildren(new PercentageCommand(this));
    }
}

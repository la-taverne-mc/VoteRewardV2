package fr.lataverne.votereward.commands.admin.rewardsgroup.reward.modify;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.DynamicCommand;
import org.jetbrains.annotations.NotNull;

public class DynRewardListAdminCommand extends CompositeCommand implements DynamicCommand {

    public DynRewardListAdminCommand(@NotNull CompositeCommand parent) {
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

        new PercentageAdminCommand(this);
    }
}

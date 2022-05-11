package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup;

import fr.lataverne.votereward.utils.commands.CompositeCommand;
import fr.lataverne.votereward.utils.commands.DynamicCommand;
import org.jetbrains.annotations.NotNull;

public class DynRewardsGroupCommand extends DynamicCommand {

    public DynRewardsGroupCommand(@NotNull CompositeCommand parent) {
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
        this.addChildren(new SeeCommand(this));
        this.addChildren(new RewardsCommand(this));
    }
}

package fr.lataverne.votereward.commands.votereward;

import fr.lataverne.votereward.commands.votereward.admin.BagCommand;
import fr.lataverne.votereward.commands.votereward.admin.FakeVoteCommand;
import fr.lataverne.votereward.commands.votereward.admin.RewardsGroupCommand;
import fr.lataverne.votereward.commands.votereward.admin.RewardsGroupsCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.jetbrains.annotations.NotNull;

public class AdminCommand extends CompositeCommand {

    public AdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "admin");
    }

    @Override
    protected void setup() {
        this.addChildren(new FakeVoteCommand(this));
        this.addChildren(new BagCommand(this));
        this.addChildren(new RewardsGroupsCommand(this));
        this.addChildren(new RewardsGroupCommand(this));
    }
}

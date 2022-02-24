package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.commands.admin.BagAdminCommand;
import fr.lataverne.votereward.commands.admin.FakeVoteCommand;
import fr.lataverne.votereward.commands.admin.RewardsGroupsAdminCommand;
import org.jetbrains.annotations.NotNull;

public class AdminCommand extends CompositeCommand {

    public AdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "admin");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin");

        new FakeVoteCommand(this);
        new BagAdminCommand(this);
        new RewardsGroupsAdminCommand(this);
    }
}

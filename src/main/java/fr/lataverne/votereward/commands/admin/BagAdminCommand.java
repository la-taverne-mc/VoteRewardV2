package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.bag.GetBagAdminCommand;
import fr.lataverne.votereward.commands.admin.bag.SeeBagAdminCommand;
import org.jetbrains.annotations.NotNull;

public class BagAdminCommand extends CompositeCommand {

    public BagAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "bag");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.bag");
        this.setOnlyPlayer(true);

        new GetBagAdminCommand(this);
        new SeeBagAdminCommand(this);
    }
}

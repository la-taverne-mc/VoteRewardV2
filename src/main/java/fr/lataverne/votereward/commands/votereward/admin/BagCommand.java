package fr.lataverne.votereward.commands.votereward.admin;

import fr.lataverne.votereward.commands.votereward.admin.bag.GetCommand;
import fr.lataverne.votereward.commands.votereward.admin.bag.SeeCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.jetbrains.annotations.NotNull;

public class BagCommand extends CompositeCommand {

    public BagCommand(@NotNull CompositeCommand parent) {
        super(parent, "bag");
    }

    @Override
    protected void setup() {
        this.setOnlyPlayer(true);

        this.addChildren(new GetCommand(this));
        this.addChildren(new SeeCommand(this));
    }
}

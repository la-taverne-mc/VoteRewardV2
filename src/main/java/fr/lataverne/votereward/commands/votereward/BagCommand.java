package fr.lataverne.votereward.commands.votereward;

import fr.lataverne.votereward.commands.votereward.bag.GetCommand;
import fr.lataverne.votereward.commands.votereward.bag.SeeCommand;
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

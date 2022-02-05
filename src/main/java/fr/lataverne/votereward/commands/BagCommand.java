package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.commands.bag.GetBagCommand;
import fr.lataverne.votereward.commands.bag.SeeBagCommand;
import org.jetbrains.annotations.NotNull;

public class BagCommand extends CompositeCommand {

    protected BagCommand(@NotNull CompositeCommand parent) {
        super(parent, "bag");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.bag");
        this.setOnlyPlayer(true);

        new GetBagCommand(this);
        new SeeBagCommand(this);
    }
}

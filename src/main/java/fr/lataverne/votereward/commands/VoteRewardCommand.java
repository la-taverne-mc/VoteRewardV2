package fr.lataverne.votereward.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoteRewardCommand extends CompositeCommand {

    public VoteRewardCommand() {
        super("votereward", "vr");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return this.showHelp(sender);
    }

    @Override
    protected void setup() {
        new BagCommand(this);
        new AdminCommand(this);
        new ConfirmCommand(this);
        new TopVoteCommand(this);
        new VoteCommand(this);

        new AddVoteConsoleCommand(this);
    }
}

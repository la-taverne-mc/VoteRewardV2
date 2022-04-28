package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.commands.common.ConfirmCommand;
import fr.lataverne.votereward.commands.votereward.AdminCommand;
import fr.lataverne.votereward.commands.votereward.BagCommand;
import fr.lataverne.votereward.commands.votereward.TopVoteCommand;
import fr.lataverne.votereward.commands.votereward.VoteCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
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
        this.addChildren(new BagCommand(this));
        this.addChildren(new AdminCommand(this));
        this.addChildren(new ConfirmCommand(this));
        this.addChildren(new TopVoteCommand(this));
        this.addChildren(new VoteCommand(this));
    }
}

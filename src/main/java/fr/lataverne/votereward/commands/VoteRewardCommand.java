package fr.lataverne.votereward.commands;

public class VoteRewardCommand extends CompositeCommand {
    public VoteRewardCommand() {
        super("votereward", "vr");
    }

    @Override
    protected void setup() {
        new BagCommand(this);
        new AdminCommand(this);
    }
}

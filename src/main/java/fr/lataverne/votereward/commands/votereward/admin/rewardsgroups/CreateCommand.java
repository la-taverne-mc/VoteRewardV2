package fr.lataverne.votereward.commands.votereward.admin.rewardsgroups;

import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateCommand extends CompositeCommand {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public CreateCommand(@NotNull CompositeCommand parent) {
        super(parent, "create");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() != 1) {
            this.misuseCommand(sender);
            return true;
        }

        String name = cmdArgs.get(0);

        if (this.plugin.getRewardsGroupManager().getRewardsGroups().containsKey(name)) {
            sender.sendMessage(this.plugin.getConfig()
                                          .getString("messages.admin.rewards-group.rewards-group-already-exists")
                                          .replace(REWARDS_GROUP_NAME, name));
            return true;
        }

        this.plugin.getRewardsGroupManager().createNewRewardsGroup(name);
        sender.sendMessage(this.plugin.getConfig()
                                      .getString("messages.admin.rewards-group.rewards-group-created")
                                      .replace(REWARDS_GROUP_NAME, name));

        return true;
    }

    @Override
    protected void setup() {
    }
}

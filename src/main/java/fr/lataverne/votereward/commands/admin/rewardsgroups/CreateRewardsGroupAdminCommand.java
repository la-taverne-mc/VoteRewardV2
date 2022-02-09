package fr.lataverne.votereward.commands.admin.rewardsgroups;

import fr.lataverne.votereward.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateRewardsGroupAdminCommand extends CompositeCommand {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public CreateRewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "create");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (args.size() != 1) {
            this.misuseCommand(sender);
            return true;
        }

        String name = args.get(0);

        if (this.plugin.getRewardsGroupManager().getRewardsGroups().containsKey(name)) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.admin.rewards-group.rewards-group-already-exists").replace(CreateRewardsGroupAdminCommand.REWARDS_GROUP_NAME, name));
            return true;
        }

        this.plugin.getRewardsGroupManager().createNewRewardsGroup(name);
        sender.sendMessage(this.plugin.getConfig().getString("messages.admin.rewards-group.rewards-group-created").replace(CreateRewardsGroupAdminCommand.REWARDS_GROUP_NAME, name));

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups.create");
    }
}

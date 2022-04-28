package fr.lataverne.votereward.commands.votereward.admin.rewardsgroups;

import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivateCommand extends CompositeCommand {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public ActivateCommand(@NotNull CompositeCommand parent) {
        super(parent, "activate");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() != 1) {
            this.misuseCommand(sender);
            return true;
        }

        String rewardsGroupName = cmdArgs.get(0);

        RewardsGroup rewardsGroup = this.plugin.getRewardsGroupManager().getRewardGroup(rewardsGroupName);
        if (rewardsGroup == null) {
            sender.sendMessage(this.plugin.getConfig()
                                          .getString("messages.admin.rewards-group.unknown-rewards-group")
                                          .replace(REWARDS_GROUP_NAME, rewardsGroupName));
            return true;
        }

        this.plugin.getRewardsGroupManager().setEnabledRewardsGroupName(rewardsGroupName);
        sender.sendMessage(this.plugin.getConfig()
                                      .getString("messages.admin.rewards-group.rewards-group-activated")
                                      .replace(REWARDS_GROUP_NAME, rewardsGroupName));

        return true;
    }

    @Override
    protected void setup() {
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
               ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
               : new ArrayList<>();
    }
}

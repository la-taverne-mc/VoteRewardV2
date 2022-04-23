package fr.lataverne.votereward.commands.admin.rewardsgroups;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivateRewardsGroupAdminCommand extends CompositeCommand {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public ActivateRewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
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
        this.setPermission("votereward.admin.rewardsgroups.activate");
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        return cmdArgs.isEmpty() || cmdArgs.size() == 1
               ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
               : new ArrayList<>();
    }
}

package fr.lataverne.votereward.commands.admin.rewardsgroup.reward.modify;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PercentageAdminCommand extends CompositeCommand {

    public PercentageAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "percentage");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() != 1) {
            this.misuseCommand(sender);
            return true;
        }

        double percentage = -1;

        try {
            percentage = Double.parseDouble(cmdArgs.get(0));
        } catch (NumberFormatException ignored) {
        }

        if (percentage < 0) {
            sender.sendMessage(this.plugin.getConfig()
                                          .getString("messages.admin.rewards-group.reward.percentage-must-be-positive-number"));
            return true;
        }

        RewardsGroup rewardsGroup = this.plugin.getRewardsGroupManager().getRewardGroup(args.get(this.level - 5));

        rewardsGroup.getAchievableReward(Integer.parseInt(args.get(this.level - 2))).setPercentage(percentage);
        sender.sendMessage(this.plugin.getConfig().getString("messages.admin.rewards-group.reward.percentage-updated"));

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup.<rewards-group-name>.reward.modify.percentage");
    }
}

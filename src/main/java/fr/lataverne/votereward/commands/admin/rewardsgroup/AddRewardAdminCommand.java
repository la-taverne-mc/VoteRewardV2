package fr.lataverne.votereward.commands.admin.rewardsgroup;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddRewardAdminCommand extends CompositeCommand {

    private static final String PERCENTAGE = "[percentage]";

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public AddRewardAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "addreward", "add");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() > 1) {
            this.misuseCommand(sender);
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        double percentage = -1.0;

        if (cmdArgs.size() == 1) {
            if (NumberUtils.isNumber(cmdArgs.get(0))) {
                percentage = NumberUtils.toDouble(cmdArgs.get(0));
            } else {
                this.misuseCommand(sender);
                return true;
            }
        }

        if (item == null || item.getType().isAir()) {
            sender.sendMessage(this.plugin.getConfig()
                                          .getString("messages.admin.rewards-group.add-reward.nothing-in-main-hand"));
        } else {
            String rewardsGroupName = args.get(this.level - 2);
            RewardsGroup rewardsGroup = this.plugin.getRewardsGroupManager().getRewardGroup(rewardsGroupName);

            Reward reward = Reward.getReward(item);

            AchievableReward achievableReward = Double.compare(percentage, -1.0) == 0
                                                ? rewardsGroup.addAchievableReward(reward)
                                                : rewardsGroup.addAchievableReward(reward, percentage);

            String message = this.plugin.getConfig()
                                        .getString("messages.admin.rewards-group.add-reward.successfully-added-reward");
            message = message.replace(REWARDS_GROUP_NAME, rewardsGroupName)
                             .replace(PERCENTAGE, Double.toString(achievableReward.getPercentage()));

            sender.sendMessage(message);
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup.<rewards-group-name>.addreward");
        this.setOnlyPlayer(true);
    }
}

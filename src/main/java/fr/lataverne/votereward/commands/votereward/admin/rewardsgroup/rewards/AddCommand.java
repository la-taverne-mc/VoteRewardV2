package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards;

import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.objects.rewards.Reward;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddCommand extends CompositeCommand {

    private static final String PERCENTAGE = "[percentage]";

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public AddCommand(@NotNull CompositeCommand parent) {
        super(parent, "add");
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
            String rewardsGroupName = args.get(this.level - 3);
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
        this.setOnlyPlayer(true);
    }
}

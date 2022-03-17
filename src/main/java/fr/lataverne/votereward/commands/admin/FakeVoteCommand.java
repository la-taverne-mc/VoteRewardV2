package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FakeVoteCommand extends CompositeCommand {

    private static final String NB_FAKEVOTE = "[nb-fakevote]";

    private static final String NB_GIVEN_REWARDS = "[nb-given-rewards]";

    public FakeVoteCommand(@NotNull CompositeCommand parent) {
        super(parent, "fakevote", "fk");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs != null && cmdArgs.size() > 1) {
            this.misuseCommand(sender);
            return true;
        }

        int amount = 1;

        if (cmdArgs != null && cmdArgs.size() == 1) {
            if (NumberUtils.isDigits(cmdArgs.get(0))) {
                amount = NumberUtils.toInt(cmdArgs.get(0));
            } else {
                this.misuseCommand(sender);
                return true;
            }
        }

        if (amount < 1) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.admin.fakevote.must-greater-than-0"));
        } else {
            Player player = (Player) sender;

            Bag bag = this.plugin.getBagManager().getOrCreateBag(player.getUniqueId());

            int nbGivenRewards = 0;

            for (int i = 0; i < amount; i++) {
                Reward reward = this.plugin.getRewardsGroupManager().getRandomReward();
                if (reward != null) {
                    bag.addReward(reward);
                    nbGivenRewards++;
                }
            }

            String message;
            if (amount > 1) {
                message = Objects.requireNonNullElse(this.plugin.getConfig()
                                                                .getString("messages.admin.fakevote.multiple"), "messages.admin.fakevote.multiple");
                message = message.replace(FakeVoteCommand.NB_FAKEVOTE, Integer.toString(amount))
                                 .replace(FakeVoteCommand.NB_GIVEN_REWARDS, Integer.toString(nbGivenRewards));
            } else {
                message = Objects.requireNonNullElse(this.plugin.getConfig()
                                                                .getString("messages.admin.fakevote.one"), "messages.admin.fakevote.one");
                message = message.replace(FakeVoteCommand.NB_GIVEN_REWARDS, Integer.toString(nbGivenRewards));
            }

            sender.sendMessage(message);
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.fakevote");
        this.setOnlyPlayer(true);
    }
}

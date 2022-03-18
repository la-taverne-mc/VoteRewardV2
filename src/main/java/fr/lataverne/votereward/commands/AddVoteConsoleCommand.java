package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddVoteConsoleCommand extends CompositeCommand {

    protected AddVoteConsoleCommand(@NotNull CompositeCommand parent) {
        super(parent, "addVote");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.size() != 1) {
                this.misuseCommand(sender);
                return true;
            }

            String username = args.get(0);

            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

            if (offlinePlayer.hasPlayedBefore()) {
                Reward reward = VoteReward.getInstance().getRewardsGroupManager().getRandomReward();
                if (reward != null) {
                    Bag bag = VoteReward.getInstance().getBagManager().getOrCreateBag(offlinePlayer.getUniqueId());
                    bag.addReward(reward);
                }
            } else {
                VoteReward.sendMessageToConsole(" [WARN] " + ChatColor.YELLOW + "A player named " + username +
                                                " voted but he has never played on the server");
            }
        } else {
            return this.parent.execute(sender, label, args);
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setHidden(true);
    }
}

package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.rewards.Reward;
import fr.lataverne.votereward.objects.votes.Vote;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public class VoteRewardConsole extends CompositeCommand {

    public VoteRewardConsole() {
        super("votereward-console", "vr-console", "vrc");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (sender instanceof ConsoleCommandSender) {
            List<String> cmdArgs = args.subList(this.level, args.size());
            if (cmdArgs.size() != 1) {
                this.parent.misuseCommand(sender);
                return true;
            }

            String username = cmdArgs.get(0);

            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

            if (offlinePlayer.hasPlayedBefore()) {
                VoteReward.getInstance()
                          .getVotingUserManager()
                          .getVotingUser(offlinePlayer.getUniqueId())
                          .addVote(new Vote(LocalDate.now()));

                String message;

                Reward reward = VoteReward.getInstance().getRewardsGroupManager().getRandomReward();
                if (reward != null) {
                    Bag bag = VoteReward.getInstance().getBagManager().getOrCreateBag(offlinePlayer.getUniqueId());
                    bag.addReward(reward);
                    message = VoteReward.getInstance().getConfig().getString("messages.reward.added.one");
                } else {
                    message = VoteReward.getInstance().getConfig().getString("messages.reward.added.any");
                }

                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(message);
                }
            } else {
                VoteReward.sendMessageToConsole(" [WARN] " + ChatColor.YELLOW + "A player named " + username +
                                                " voted but he has never played on the server");
            }
        } else {
            sender.sendMessage(this.plugin.getConfig()
                                          .getString("messages.error.no-permission")
                                          .replace("[permission]", this.getPermission()));
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setHidden(true);
    }
}

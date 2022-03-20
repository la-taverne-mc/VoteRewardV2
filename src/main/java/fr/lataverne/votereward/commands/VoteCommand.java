package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.votes.ETimeRange;
import fr.lataverne.votereward.objects.votes.VotingUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VoteCommand extends CompositeCommand {

    private static final String NB_VOTES = "[nb-votes]";

    private static final String PLAYER = "[player]";

    public VoteCommand(@NotNull CompositeCommand parent) {
        super(parent, "vote");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.isEmpty() || cmdArgs.size() > 2) {
            this.misuseCommand(sender);
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cmdArgs.get(0));

        if (offlinePlayer.hasPlayedBefore()) {
            ETimeRange timeRange = ETimeRange.ALL_TIME;

            if (cmdArgs.size() > 1) {
                try {
                    timeRange = ETimeRange.valueOf(cmdArgs.get(1).toUpperCase(Locale.ENGLISH).replace("-", "_"));
                } catch (IllegalArgumentException ignored) {
                    this.misuseCommand(sender);
                    return true;
                }
            }

            VotingUser votingUser = VoteReward.getInstance()
                                              .getVotingUserManager()
                                              .getVotingUser(offlinePlayer.getUniqueId());

            int nbVotes = votingUser != null
                          ? votingUser.getVotes(timeRange).size()
                          : 0;

            String messagePath = votingUser != null && nbVotes > 0
                                 ? "messages.vote.one-or-more"
                                 : "messages.vote-command.never";

            messagePath += "." + timeRange;

            sender.sendMessage(VoteReward.getInstance()
                                         .getConfig()
                                         .getString(messagePath)
                                         .replace(PLAYER, offlinePlayer.getName())
                                         .replace(NB_VOTES, Integer.toString(nbVotes)));
        } else {
            sender.sendMessage(VoteReward.getInstance()
                                         .getConfig()
                                         .getString("messages.error.unknown-player")
                                         .replace(PLAYER, cmdArgs.get(0)));
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.vote");
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (args.isEmpty() || args.size() == 1) {
            return VoteReward.getInstance()
                             .getVotingUserManager()
                             .getVotingUsers()
                             .stream()
                             .filter(v -> !v.getVotes().isEmpty())
                             .map(v -> Bukkit.getOfflinePlayer(v.getUUID()).getName())
                             .toList();
        } else if (args.size() == 2) {
            return Arrays.stream(ETimeRange.values()).map(ETimeRange::toString).toList();
        } else {
            return new ArrayList<>();
        }
    }
}

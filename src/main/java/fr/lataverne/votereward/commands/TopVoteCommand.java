package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.votes.ETopVoteArg;
import fr.lataverne.votereward.objects.votes.VotingUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TopVoteCommand extends CompositeCommand {

    private static final String CURRENT_PAGE = "[current-page]";

    private static final String LAST_PAGE = "[last-page]";

    private static final String NB_VOTES = "[nb-votes]";

    private static final int NB_VOTING_USERS_TO_DISPLAY = 10;

    private static final String RANK = "[rank]";

    private static final String USERNAME = "[username]";

    public TopVoteCommand(@NotNull CompositeCommand parent) {
        super(parent, "topvote");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() > 2) {
            this.misuseCommand(sender);
            return true;
        }

        ETopVoteArg topVoteArg = ETopVoteArg.MONTH;
        int page = 1;

        try {
            if (!cmdArgs.isEmpty()) {
                topVoteArg = ETopVoteArg.valueOf(cmdArgs.get(0).toUpperCase(Locale.ENGLISH).replace("-", "_"));
            }

            if (cmdArgs.size() > 1) {
                page = Integer.parseInt(cmdArgs.get(1));
                if (page == 0) {
                    page = 1;
                }
            }
        } catch (IllegalArgumentException ignored) {
            this.misuseCommand(sender);
            return true;
        }

        Collection<VotingUser> votingUsers = VoteReward.getInstance().getVotingUserManager().getVotingUsers(topVoteArg);

        int lastPage = votingUsers.size() / NB_VOTING_USERS_TO_DISPLAY +
                       (votingUsers.size() % NB_VOTING_USERS_TO_DISPLAY == 0
                        ? 0
                        : 1);

        if (page > lastPage) {
            page = lastPage;
        }

        List<VotingUser> votingUsersToDisplay = getTopVotingUserToDisplay(votingUsers, topVoteArg, page);

        displayTopVote(sender, votingUsersToDisplay, topVoteArg, page, lastPage);

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.topvote");
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
               ? Arrays.asList("all-time", "year", "month", "week", "day")
               : new ArrayList<>();
    }

    private static int compareVotingUsers(@NotNull VotingUser votingUser1, @NotNull VotingUser votingUser2, ETopVoteArg topVoteArg) {
        int nbVotes1 = votingUser1.getVotes(topVoteArg).size();
        int nbVotes2 = votingUser2.getVotes(topVoteArg).size();

        return Integer.compare(nbVotes2, nbVotes1);
    }

    private static void displayTopVote(@NotNull CommandSender sender, @NotNull Iterable<? extends VotingUser> votingUsers, ETopVoteArg topVoteArg, int currentPage, int lastPage) {
        String headerPath = switch (topVoteArg) {
            case ALL_TIME -> "messages.topvote.header.all-time";
            case YEAR -> "messages.topvote.header.year";
            case MONTH -> "messages.topvote.header.month";
            case WEEK -> "messages.topvote.header.week";
            case DAY -> "messages.topvote.header.day";
        };

        sender.sendMessage("");
        sender.sendMessage(VoteReward.getInstance().getConfig().getString(headerPath));

        int rank = ((currentPage - 1) * NB_VOTING_USERS_TO_DISPLAY) + 1;

        for (VotingUser votingUser : votingUsers) {
            String votingUserLinePath = switch (rank) {
                case 1 -> "messages.topvote.voting-user-line.first";
                case 2 -> "messages.topvote.voting-user-line.second";
                case 3 -> "messages.topvote.voting-user-line.third";
                default -> "messages.topvote.voting-user-line.other";
            };

            String username = Bukkit.getOfflinePlayer(votingUser.getUUID()).getName();
            int nbVotes = votingUser.getVotes(topVoteArg).size();

            String message = VoteReward.getInstance()
                                       .getConfig()
                                       .getString(votingUserLinePath)
                                       .replace(RANK, Integer.toString(rank))
                                       .replace(USERNAME, username)
                                       .replace(NB_VOTES, Integer.toString(nbVotes));

            sender.sendMessage(message);

            rank++;
        }

        sender.sendMessage(VoteReward.getInstance()
                                     .getConfig()
                                     .getString("messages.topvote.footer")
                                     .replace(CURRENT_PAGE, Integer.toString(currentPage))
                                     .replace(LAST_PAGE, Integer.toString(lastPage)));
    }

    private static @NotNull List<VotingUser> getTopVotingUserToDisplay(@NotNull Collection<VotingUser> votingUsers, ETopVoteArg topVoteArg, int page) {
        List<VotingUser> votingUsersToDisplay = votingUsers.stream()
                                                           .sorted(((votingUser1, votingUser2) -> compareVotingUsers(votingUser1, votingUser2, topVoteArg)))
                                                           .toList();

        int first = (page - 1) * NB_VOTING_USERS_TO_DISPLAY;
        int last = first + NB_VOTING_USERS_TO_DISPLAY;

        if (votingUsersToDisplay.size() < last) {
            last = votingUsersToDisplay.size();
        }

        return votingUsersToDisplay.subList(first, last);
    }
}

package fr.lataverne.votereward.managers;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public record VotifierManager(BagManager bagManager, RewardsGroupManager rewardsGroupManager) implements Listener {

    @Override
    public @NotNull String toString() {
        return "VotifierManager{}";
    }

    @EventHandler
    public void voteListener(@NotNull VotifierEvent e) {
        Vote vote = e.getVote();
        String username = vote.getUsername();

        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (offlinePlayer.hasPlayedBefore()) {
            Reward reward = this.rewardsGroupManager.getRandomReward();
            if (reward != null) {
                Bag bag = this.bagManager.getOrCreateBag(offlinePlayer.getUniqueId());
                bag.addReward(reward);
            }
        }
    }
}

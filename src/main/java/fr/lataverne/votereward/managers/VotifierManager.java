package fr.lataverne.votereward.managers;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class VotifierManager implements Listener {
	@EventHandler
	public static void voteListener(@NotNull final VotifierEvent e) {
		Vote vote = e.getVote();
		String username = vote.getUsername();

		@SuppressWarnings ("deprecation") OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

		if (offlinePlayer.hasPlayedBefore()) {
			LocalDate expirationDate = LocalDate.now().plusDays(Constant.EXPIRATION_TIME);
			AchievableReward achievableReward = AchievableReward.getRandomReward();
			if (achievableReward == null) {
				return;
			}

			ItemStack clone = new ItemStack(achievableReward.itemStack());
			Reward reward = new Reward(clone, expirationDate, achievableReward.id());
			Bag.getPlayerBag(offlinePlayer.getUniqueId()).addNewReward(reward);
		}
	}
}

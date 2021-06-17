package fr.lataverne.votereward.managers;

import com.vexsoftware.votifier.model.VotifierEvent;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.time.LocalDate;

public class VotifierManager {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void voteListener(VotifierEvent e)
    {
        if(Bukkit.getOfflinePlayer(e.getVote().getUsername()).hasPlayedBefore())
        {
            LocalDate expirationDate = LocalDate.now().plusDays(15);
            AchievableReward achievableReward = AchievableReward.getRandomReward();
            if (achievableReward == null) {
                return;
            }
            Reward reward = new Reward(achievableReward.getItemStack().clone(), expirationDate, achievableReward.getId());
            Bag.getPlayerBag(Bukkit.getOfflinePlayer(e.getVote().getUsername()).getUniqueId()).addNewReward(reward);
        }
    }
}

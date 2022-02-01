package fr.lataverne.votereward.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.utils.collections.RandomCollection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RewardGroup {
    private static final double DEFAULT_PERCENTAGE_REWARD = 5;

    private final Collection<AchievableReward> achievableRewards;

    public RewardGroup(@NotNull List<AchievableReward> achievableRewards) {
        this.achievableRewards = new ArrayList<>(achievableRewards);
    }

    public static @Nullable RewardGroup parseJson(@NotNull JsonElement elemRewardGroup) {
        if (elemRewardGroup.isJsonArray()) {
            JsonArray jsonRewardGroup = elemRewardGroup.getAsJsonArray();

            ArrayList<AchievableReward> achievableRewards = new ArrayList<>();
            jsonRewardGroup.forEach(jsonAchievableReward -> {
                AchievableReward achievableReward = AchievableReward.parseJson(jsonAchievableReward);
                if (achievableReward != null) {
                    achievableRewards.add(achievableReward);
                }
            });

            return new RewardGroup(achievableRewards);
        }

        return null;
    }

    public void addAchievableReward(ItemStack item) {
        this.addAchievableReward(item, RewardGroup.DEFAULT_PERCENTAGE_REWARD);
    }

    public void addAchievableReward(ItemStack item, double percentage) {
        this.achievableRewards.add(new AchievableReward(item, percentage));
    }

    public int getNumberOfReward() {
        return this.achievableRewards.size();
    }

    public @Nullable Reward getRandomReward() {
        if (this.achievableRewards.isEmpty()) {
            return null;
        }

        RandomCollection<AchievableReward> randomCollection = new RandomCollection<>();
        for (AchievableReward achievableReward : this.achievableRewards) {
            randomCollection.add(achievableReward.percentage(), achievableReward);
        }

        AchievableReward achievableReward = randomCollection.next();

        if (achievableReward != null) {
            return new Reward(achievableReward.reward(), LocalDate.now().plusDays(Constant.EXPIRATION_TIME));
        } else {
            return null;
        }
    }

    public JsonElement toJson() {
        JsonArray jsonRewardGroup = new JsonArray();

        this.achievableRewards.forEach(achievableReward -> jsonRewardGroup.add(achievableReward.toJson()));

        return jsonRewardGroup;
    }

    @Override
    public String toString() {
        return "RewardGroup{" +
                "achievableRewards=" + this.achievableRewards +
                "}";
    }
}

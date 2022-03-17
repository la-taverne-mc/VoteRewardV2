package fr.lataverne.votereward.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.lataverne.votereward.objects.rewards.Reward;
import fr.lataverne.votereward.utils.collections.RandomCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RewardsGroup {

    private static final double DEFAULT_PERCENTAGE_REWARD = 5;

    private final Map<Integer, AchievableReward> achievableRewards;

    public RewardsGroup(@NotNull List<AchievableReward> achievableRewards) {
        this.achievableRewards = new HashMap<>();
        int size = achievableRewards.size();
        for (int i = 0; i < size; i++) {
            this.achievableRewards.put(i, achievableRewards.get(i));
        }
    }

    public static @Nullable RewardsGroup parseJson(@NotNull JsonElement elemRewardGroup) {
        if (elemRewardGroup.isJsonArray()) {
            JsonArray jsonRewardGroup = elemRewardGroup.getAsJsonArray();

            ArrayList<AchievableReward> achievableRewards = new ArrayList<>();
            jsonRewardGroup.forEach(jsonAchievableReward -> {
                AchievableReward achievableReward = AchievableReward.parseJson(jsonAchievableReward);
                if (achievableReward != null) {
                    achievableRewards.add(achievableReward);
                }
            });

            return new RewardsGroup(achievableRewards);
        }

        return null;
    }

    public AchievableReward addAchievableReward(Reward reward) {
        return this.addAchievableReward(reward, RewardsGroup.DEFAULT_PERCENTAGE_REWARD);
    }

    public AchievableReward addAchievableReward(Reward reward, double percentage) {
        AchievableReward achievableReward = new AchievableReward(reward, percentage);
        this.achievableRewards.put(this.getAvailableId(), achievableReward);
        return achievableReward;
    }

    public Collection<AchievableReward> getAchievableRewards() {
        return Collections.unmodifiableCollection(this.achievableRewards.values());
    }

    public int getNbRewards() {
        return this.achievableRewards.size();
    }

    public @Nullable Reward getRandomReward() {
        if (this.achievableRewards.isEmpty()) {
            return null;
        }

        RandomCollection<AchievableReward> randomCollection = new RandomCollection<>();
        for (AchievableReward achievableReward : this.achievableRewards.values()) {
            randomCollection.add(achievableReward.percentage(), achievableReward);
        }

        AchievableReward achievableReward = randomCollection.next();

        return achievableReward != null
               ? achievableReward.reward()
               : null;
    }

    public double getRealPercentageOfReward(AchievableReward achievableReward) {
        if (!this.achievableRewards.containsValue(achievableReward)) {
            return -1;
        }

        double total = this.achievableRewards.values().stream().mapToDouble(AchievableReward::percentage).sum();

        return 100.0 * achievableReward.percentage() / total;
    }

    public JsonElement toJson() {
        JsonArray jsonRewardGroup = new JsonArray();

        this.achievableRewards.forEach((id, achievableReward) -> jsonRewardGroup.add(achievableReward.toJson()));

        return jsonRewardGroup;
    }

    @Override
    public String toString() {
        return "RewardGroup{" + "achievableRewards=" + this.achievableRewards + "}";
    }

    private Integer getAvailableId() {
        int id = 0;
        boolean idNotAvailable = true;

        while (idNotAvailable) {
            if (this.achievableRewards.containsKey(id)) {
                id++;
            } else {
                idNotAvailable = false;
            }
        }

        return id;
    }
}

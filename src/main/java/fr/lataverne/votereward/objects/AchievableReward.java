package fr.lataverne.votereward.objects;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record AchievableReward(ItemStack itemStack, double percentage, int id) {
    private static final HashMap<Integer, AchievableReward> achievableRewards = new HashMap<>();

    public static void addNewAchievableRewards(final AchievableReward achievableReward, final int id) {
        AchievableReward.achievableRewards.put(id, achievableReward);
    }

    public static void clear() {
        AchievableReward.achievableRewards.clear();
    }

    public static AchievableReward getAchievableReward(final int id) {
        return AchievableReward.achievableRewards.get(id);
    }

    public static AchievableReward[] getAchievableRewards() {
        return AchievableReward.achievableRewards.values().toArray(new AchievableReward[0]);
    }

    public static int getNumberOfAchievableRewards() {
        return AchievableReward.achievableRewards.size();
    }

    public static @Nullable AchievableReward getRandomReward() {
        if (AchievableReward.achievableRewards.isEmpty()) {
            return null;
        }

        List<Integer> randomized = new ArrayList<>();

        for (final Map.Entry<Integer, AchievableReward> entry : AchievableReward.achievableRewards.entrySet()) {
            AchievableReward value = entry.getValue();
            for (int i = 0; i < value.percentage * 100; i++) {
                randomized.add(entry.getKey());
            }
        }

        int randomIndex = randomized.get(new SecureRandom().nextInt(randomized.size() - 1)).intValue();
        return AchievableReward.achievableRewards.get(randomIndex);
    }

    public double getRealChanceOfDrop() {
        int total = 0;
        for (final AchievableReward achievableReward : AchievableReward.achievableRewards.values()) {
            total += achievableReward.percentage * 100;
        }

        return (this.percentage * 100) * 100 / total;
    }
}

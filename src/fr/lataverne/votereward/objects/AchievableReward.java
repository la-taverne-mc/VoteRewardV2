package fr.lataverne.votereward.objects;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AchievableReward {
    private final ItemStack itemStack;
    private final double percentage;
    private final int id;

    private static final HashMap<Integer, AchievableReward> achievableRewards = new HashMap<>();

    public AchievableReward(ItemStack itemStack, double percentage, int id) {
        this.itemStack = itemStack;
        this.percentage = percentage;
        this.id = id;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public double getPercentage() {
        return this.percentage;
    }

    public double getRealChanceOfDrop() {
        int total = 0;
        for (AchievableReward achievableReward : achievableRewards.values()) {
            total += achievableReward.percentage * 100;
        }

        return (this.percentage * 100) * 100 / total;
    }

    public int getId() {
        return id;
    }

    public static AchievableReward[] getAchievableRewards() {
        return achievableRewards.values().toArray(new AchievableReward[0]);
    }

    public static AchievableReward getRandomReward() {
        if (achievableRewards.size() == 0) {
            return null;
        }

        ArrayList<Integer> randomized = new ArrayList<>();

        for (Map.Entry<Integer, AchievableReward> entry : achievableRewards.entrySet()) {
            for (int i = 0; i < entry.getValue().percentage * 100; i++) {
                randomized.add(entry.getKey());
            }
        }

        int randomIndex = randomized.get(new Random().nextInt(randomized.size() - 1));
        return achievableRewards.get(randomIndex);
    }

    public static int getNumberOfAchievableRewards() {
        return achievableRewards.size();
    }

    public static AchievableReward getAchievableReward(int id) {
        return AchievableReward.achievableRewards.get(id);
    }

    public static void addNewAchievableRewards(AchievableReward achievableReward, int id) {
        AchievableReward.achievableRewards.put(id, achievableReward);
    }

    public static void clear() {
        AchievableReward.achievableRewards.clear();
    }
}

package fr.lataverne.votereward.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AchievableReward {
	private static final HashMap<Integer, AchievableReward> achievableRewards = new HashMap<>();

	private final ItemStack itemStack;

	private final double percentage;

	private final int id;

	public AchievableReward(ItemStack itemStack, double percentage, int id) {
		this.itemStack = itemStack;
		this.percentage = percentage;
		this.id = id;
	}

	public static void addNewAchievableRewards(AchievableReward achievableReward, int id) {
		AchievableReward.achievableRewards.put(id, achievableReward);
	}

	public static void clear() {
		AchievableReward.achievableRewards.clear();
	}

	public static AchievableReward getAchievableReward(int id) {
		return AchievableReward.achievableRewards.get(id);
	}

	public static AchievableReward[] getAchievableRewards() {
		return achievableRewards.values().toArray(new AchievableReward[0]);
	}

	public static int getNumberOfAchievableRewards() {
		return achievableRewards.size();
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

	public int getId() {
		return id;
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
}

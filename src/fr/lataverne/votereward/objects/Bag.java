package fr.lataverne.votereward.objects;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Bag {

	private static final HashMap<UUID, Bag> bags = new HashMap<>();

	private final ArrayList<Reward> bagContent;

	private final UUID owner;

	public Bag(ArrayList<Reward> inventoryContent, UUID owner) {
		this.bagContent = inventoryContent;
		this.owner = owner;
	}

	public static void clear() {
		Bag.bags.clear();
	}

	public static Bag[] getBags() {
		return bags.values().toArray(new Bag[0]);
	}

	public static Bag getPlayerBag(UUID uuid) {
		Bag bag = Bag.bags.containsKey(uuid) ? Bag.bags.get(uuid) : createPlayerBag(uuid);
		bag.checkExpirationDates();
		return bag;
	}

	public static void retrievingBag(Bag bag, Player player) {
		retrievingBag(bag, player, 35);
	}

	public static void retrievingBag(Bag bag, Player player, int maxNbRewardsRetrieving) {

		if (bag.getBagContent().size() == 0) {
			Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.noRewardToBeRetrieved"));
			return;
		}

		int nbRewardsRetrieving = 0;
		int nbItemInBag = bag.getBagContent().size();

		while (!Helper.inventoryPlayerIsFull(player) && nbRewardsRetrieving < nbItemInBag && nbRewardsRetrieving < maxNbRewardsRetrieving) {
			Reward reward = bag.getRandomReward();
			player.getInventory().addItem(reward.getItemStack().clone());
			bag.removeReward(reward);
			nbRewardsRetrieving++;
		}

		String message = Helper.replaceValueInString(Helper.getMessageOnConfig("player.retrieveRewards"), Integer.toString(nbRewardsRetrieving));
		Helper.sendMessageToPlayer(player, message);
	}

	public void addNewReward(Reward item) {
		this.bagContent.add(item);
	}

	public ArrayList<Reward> getBagContent() {
		return this.bagContent;
	}

	public Reward getRandomReward() {
		if (this.bagContent.size() > 0) {
			return this.bagContent.get(new Random().nextInt(bagContent.size()));
		} else {
			return null;
		}
	}

	public void removeReward(Reward reward) {
		this.bagContent.remove(reward);
	}

	public void saveBag() {
		Path path = Paths.get(VoteReward.getInstance().getConfig().getString("system.bagsDirectory") + owner + ".yml");

		if (Files.exists(path)) {
			try {
				Files.delete(path);
			} catch (Exception ignored) {
				String message = Helper.replaceValueInString(VoteReward.getInstance().getConfig().getString("message.system.deleteFileFailed"), path.toString());
				VoteReward.sendMessageToConsole(message);
			}
		}

		if (bagContent.size() == 0) {
			return;
		}

		try {
			Files.createFile(path);

			for (int i = 0; i < bagContent.size(); i++) {
				Reward reward = bagContent.get(i);
				String strReward = "reward_" + i + ":\n" + "  type: " + reward.getItemStack().getType().name() + "\n" + "  amount: " + reward.getItemStack().getAmount() + "\n" + "  ID: " + reward.getAchievableRewardId() + "\n" + "  expiration: " + reward.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n";

				Files.write(path, strReward.getBytes(), StandardOpenOption.APPEND);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Bag createPlayerBag(UUID uuid) {
		Bag newBag = new Bag(new ArrayList<>(), uuid);
		Bag.bags.put(uuid, newBag);
		return newBag;
	}

	private void checkExpirationDates() {
		if (this.bagContent.size() > 0) {
			this.bagContent.removeIf(reward -> reward.getExpirationDate().isBefore(LocalDate.now()));
		}
	}
}

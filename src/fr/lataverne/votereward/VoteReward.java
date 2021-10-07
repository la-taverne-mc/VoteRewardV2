package fr.lataverne.votereward;

import fr.lataverne.votereward.managers.CommandManager;
import fr.lataverne.votereward.managers.EventListener;
import fr.lataverne.votereward.managers.InternalPermission;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Main class that extends JavaPlugin.
 */
public class VoteReward extends JavaPlugin {
	private static VoteReward instance;

	public static VoteReward getInstance() {
		return VoteReward.instance;
	}

	public static void sendMessageToConsole(String message) {
		String str = instance.getConfig().getString("message.consoleSuffix") + " " + ChatColor.RESET + message;
		Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
	}

	@Override
	public void onDisable() {
		saveBagOfAllPlayers();
		sendMessageToConsole(getConfig().getString("message.system.stopMessage"));
	}

	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {
		instance = this;

		Path bagsDirectory = Paths.get(Objects.requireNonNull(VoteReward.getInstance().getConfig().getString("system.bagsDirectory")));
		if (!Files.exists(bagsDirectory)) {
			try {
				Files.createDirectory(bagsDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		CommandManager commandManager = new CommandManager();
		Objects.requireNonNull(getCommand("votereward")).setExecutor(commandManager);

		EventListener eventListener = new EventListener();
		Bukkit.getPluginManager().registerEvents(eventListener, this);

		sendMessageToConsole(getConfig().getString("message.system.startMessage"));
	}

	/**
	 * Method for reload the plugin's config.
	 * If the config don't exist then we save the default config.
	 */
	@Override
	public void reloadConfig() {
		boolean configExist = true;
		if (!new File("plugins/VoteReward/config.yml").exists()) {
			saveDefaultConfig();
			configExist = false;
		}

		super.reloadConfig();

		if (configExist) {
			sendMessageToConsole(getConfig().getString("message.system.existingConfig"));
		} else {
			sendMessageToConsole(getConfig().getString("message.system.nonExistingConfig"));
		}

		loadAchievableRewards();
		loadBags();

		sendMessageToConsole(getConfig().getString("message.system.reloadComplete"));
	}

	private static void addExpirationDate(LocalDate expiration, ItemStack rewardItem, ItemMeta rewardItemMeta) {
		List<String> rewardItemLore = rewardItemMeta.getLore();
		if (rewardItemLore == null) {
			rewardItemLore = new ArrayList<>();
		}

		rewardItemLore.add(ChatColor.BLUE + "Expiration : " + expiration);

		rewardItemMeta.setLore(rewardItemLore);
		rewardItem.setItemMeta(rewardItemMeta);
	}

	private static void loadAchievableRewards() {
		String dataFilePath = instance.getConfig().getString("system.achievableRewardsPath");
		File dataFile = null;
		if (dataFilePath != null) {
			dataFile = new File(dataFilePath);
		}

		if (dataFilePath == null || !dataFile.exists()) {
			sendMessageToConsole(instance.getConfig().getString("message.system.achievableRewardsFileNotFound") + " (Path: " + dataFilePath + ")");
			return;
		}

		AchievableReward.clear();

		YamlConfiguration achievableRewardFile = new YamlConfiguration();
		try {
			achievableRewardFile.load(dataFile);
			for (String key : achievableRewardFile.getKeys(false)) {
				String type = achievableRewardFile.getString(key + ".type");
				Material materialType;
				if (type != null && (materialType = Material.getMaterial(type)) != null) {
					int amount = achievableRewardFile.contains(key + ".amount") ? achievableRewardFile.getInt(key + ".amount") : 1;
					double percentage = achievableRewardFile.contains(key + ".percent") ? achievableRewardFile.getDouble(key + ".percent") : 0.0;
					int id = Integer.parseInt(key.replace("reward_", ""));

					ItemStack itemStack = new ItemStack(materialType, amount);

					AchievableReward.addNewAchievableRewards(new AchievableReward(itemStack, percentage, id), id);
				}
			}
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadBags() {
		String dataDirectoryPath = instance.getConfig().getString("system.bagsDirectory");
		File dataDirectory = null;
		if (dataDirectoryPath != null) {
			dataDirectory = new File(dataDirectoryPath);
		}

		if (dataDirectoryPath == null || !dataDirectory.exists()) {
			sendMessageToConsole(instance.getConfig().getString("message.system.bagsDirectoryNotFound"));
			return;
		}

		File[] fileList = dataDirectory.listFiles();

		if (fileList == null) {
			return;
		}

		sendMessageToConsole(instance.getConfig().getString("message.system.bagsLoading"));

		for (File file : fileList) {
			YamlConfiguration bagContent = new YamlConfiguration();
			try {
				bagContent.load(file);

				UUID owner = UUID.fromString(file.getName().replace(".yml", ""));

				Bag.clear();

				Bag playerBag = Bag.getPlayerBag(owner);

				for (String key : bagContent.getKeys(false)) {
					int id;
					if (bagContent.contains(key + ".ID") && (id = bagContent.getInt(key + ".ID")) != -1) {
						AchievableReward achievableReward = AchievableReward.getAchievableReward(id);
						if (achievableReward != null) {
							String expirationString = bagContent.getString(key + ".expiration");
							if (expirationString == null) {
								expirationString = "01/01/1975";
							}

							LocalDate expiration = LocalDate.parse(expirationString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

							ItemStack rewardItem = achievableReward.getItemStack().clone();
							ItemMeta rewardItemMeta = rewardItem.getItemMeta();
							if (rewardItemMeta != null) {
								addExpirationDate(expiration, rewardItem, rewardItemMeta);

								playerBag.addNewReward(new Reward(rewardItem, expiration, id));
							}
						}
					} else {
						String type = bagContent.getString(key + ".type");
						Material materialType;
						if (type != null && (materialType = Material.getMaterial(type)) != null) {
							int amount = bagContent.contains(key + ".amount") ? bagContent.getInt(key + ".amount") : 1;
							String expirationString = bagContent.getString(key + ".expiration");
							if (expirationString == null) {
								expirationString = "01/01/1975";
							}

							LocalDate expiration = LocalDate.parse(expirationString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

							ItemStack rewardItem = new ItemStack(materialType, amount);
							ItemMeta rewardItemMeta = rewardItem.getItemMeta();
							if (rewardItemMeta != null) {
								addExpirationDate(expiration, rewardItem, rewardItemMeta);

								playerBag.addNewReward(new Reward(rewardItem, expiration, -1));
							}
						}
					}
				}

			} catch (InvalidConfigurationException | IOException e) {
				e.printStackTrace();
			}
		}

		sendMessageToConsole(instance.getConfig().getString("message.system.bagsLoadingSucceeds"));
	}

	private static void saveBagOfAllPlayers() {
		for (Bag bag : Bag.getBags()) {
			bag.saveBag();
		}
	}
}

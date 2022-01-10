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
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
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

@SuppressWarnings ("ClassNamePrefixedWithPackageName")
public class VoteReward extends JavaPlugin {
	private static VoteReward instance = null;

	public static VoteReward getInstance() {
		return VoteReward.instance;
	}

	public static void sendMessageToConsole(final String message) {
		String str = VoteReward.instance.getConfig().getString("message.consoleSuffix") + " " + ChatColor.RESET + message;
		Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
	}

	@Override
	public void onDisable() {
		VoteReward.saveBagOfAllPlayers();
		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.stopMessage"));
	}

	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		VoteReward.instance = this;

		Path bagsDirectory = Paths.get(Objects.requireNonNull(VoteReward.getInstance().getConfig().getString("system.bagsDirectory")));
		if (!Files.exists(bagsDirectory)) {
			try {
				Files.createDirectory(bagsDirectory);
			} catch (final IOException ignored) {
			}
		}

		CommandExecutor commandManager = new CommandManager();
		Objects.requireNonNull(this.getCommand("votereward")).setExecutor(commandManager);

		Listener eventListener = new EventListener();
		Bukkit.getPluginManager().registerEvents(eventListener, this);

		InternalPermission.loadingInternalPermissions();

		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.startMessage"));
	}

	/**
	 * Method for reload the plugin's config.
	 * If the config don't exist then we save the default config.
	 */
	@Override
	public void reloadConfig() {
		boolean configExist = true;
		if (!new File("plugins/VoteReward/config.yml").exists()) {
			this.saveDefaultConfig();
			configExist = false;
		}

		super.reloadConfig();

		if (configExist) {
			VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.existingConfig"));
		} else {
			VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.nonExistingConfig"));
		}

		VoteReward.loadAchievableRewards();
		VoteReward.loadBags();

		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.reloadComplete"));
	}

	private static void addExpirationDate(final LocalDate expiration, final ItemStack rewardItem, final ItemMeta rewardItemMeta) {
		List<String> rewardItemLore = rewardItemMeta.getLore();
		if (rewardItemLore == null) {
			rewardItemLore = new ArrayList<>();
		}

		rewardItemLore.add(ChatColor.BLUE + "Expiration : " + expiration);

		rewardItemMeta.setLore(rewardItemLore);
		rewardItem.setItemMeta(rewardItemMeta);
	}

	private static void loadAchievableRewards() {
		String dataFilePath = VoteReward.instance.getConfig().getString("system.achievableRewardsPath");
		File dataFile = null;
		if (dataFilePath != null) {
			dataFile = new File(dataFilePath);
		}

		if (dataFilePath == null || !dataFile.exists()) {
			VoteReward.sendMessageToConsole(VoteReward.instance.getConfig().getString("message.system.achievableRewardsFileNotFound") + " (Path: " + dataFilePath + ")");
			return;
		}

		AchievableReward.clear();

		YamlConfiguration achievableRewardFile = new YamlConfiguration();
		try {
			achievableRewardFile.load(dataFile);
			for (final String key : achievableRewardFile.getKeys(false)) {
				String type = achievableRewardFile.getString(key + ".type");
				if (type != null) {
					Material materialType = Material.getMaterial(type);
					if (materialType != null) {
						int amount = achievableRewardFile.contains(key + ".amount") ? achievableRewardFile.getInt(key + ".amount") : 1;
						double percentage = achievableRewardFile.contains(key + ".percent") ? achievableRewardFile.getDouble(key + ".percent") : 0.0;
						int id = Integer.parseInt(key.replace("reward_", ""));

						ItemStack itemStack = new ItemStack(materialType, amount);

						AchievableReward.addNewAchievableRewards(new AchievableReward(itemStack, percentage, id), id);
					}
				}
			}
		} catch (final IOException | InvalidConfigurationException ignored) {
		}
	}

	private static void loadBags() {
		String dataDirectoryPath = VoteReward.instance.getConfig().getString("system.bagsDirectory");
		File dataDirectory = null;
		if (dataDirectoryPath != null) {
			dataDirectory = new File(dataDirectoryPath);
		}

		if (dataDirectoryPath == null || !dataDirectory.exists()) {
			VoteReward.sendMessageToConsole(VoteReward.instance.getConfig().getString("message.system.bagsDirectoryNotFound"));
			return;
		}

		File[] fileList = dataDirectory.listFiles();

		if (fileList == null) {
			return;
		}

		VoteReward.sendMessageToConsole(VoteReward.instance.getConfig().getString("message.system.bagsLoading"));

		for (final File file : fileList) {
			YamlConfiguration bagContent = new YamlConfiguration();
			try {
				bagContent.load(file);

				UUID owner = UUID.fromString(file.getName().replace(".yml", ""));

				Bag.clear();

				Bag playerBag = Bag.getPlayerBag(owner);

				for (final String key : bagContent.getKeys(false)) {
					int id;
					if (bagContent.contains(key + ".ID") && (id = bagContent.getInt(key + ".ID")) != -1) {
						AchievableReward achievableReward = AchievableReward.getAchievableReward(id);
						if (achievableReward != null) {
							String expirationString = bagContent.getString(key + ".expirationDate");
							if (expirationString == null) {
								expirationString = "01/01/1975";
							}

							LocalDate expiration = LocalDate.parse(expirationString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

							ItemStack rewardItem = new ItemStack(achievableReward.itemStack());
							ItemMeta rewardItemMeta = rewardItem.getItemMeta();
							if (rewardItemMeta != null) {
								VoteReward.addExpirationDate(expiration, rewardItem, rewardItemMeta);

								playerBag.addNewReward(new Reward(rewardItem, expiration, id));
							}
						}
					} else {
						String type = bagContent.getString(key + ".type");
						Material materialType;
						if (type != null && (materialType = Material.getMaterial(type)) != null) {
							int amount = bagContent.contains(key + ".amount") ? bagContent.getInt(key + ".amount") : 1;
							String expirationString = bagContent.getString(key + ".expirationDate");
							if (expirationString == null) {
								expirationString = "01/01/1975";
							}

							LocalDate expiration = LocalDate.parse(expirationString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

							ItemStack rewardItem = new ItemStack(materialType, amount);
							ItemMeta rewardItemMeta = rewardItem.getItemMeta();
							if (rewardItemMeta != null) {
								VoteReward.addExpirationDate(expiration, rewardItem, rewardItemMeta);

								playerBag.addNewReward(new Reward(rewardItem, expiration, -1));
							}
						}
					}
				}
			} catch (final InvalidConfigurationException | IOException ignored) {
			}
		}

		VoteReward.sendMessageToConsole(VoteReward.instance.getConfig().getString("message.system.bagsLoadingSucceeds"));
	}

	private static void saveBagOfAllPlayers() {
		for (final Bag bag : Bag.getBags()) {
			bag.saveBag();
		}
	}
}

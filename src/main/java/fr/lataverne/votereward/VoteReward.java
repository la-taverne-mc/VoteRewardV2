package fr.lataverne.votereward;

import fr.lataverne.votereward.managers.BagManager;
import fr.lataverne.votereward.managers.CommandManager;
import fr.lataverne.votereward.managers.EventListener;
import fr.lataverne.votereward.managers.InternalPermission;
import fr.lataverne.votereward.objects.AchievableReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings ("ClassNamePrefixedWithPackageName")
public class VoteReward extends JavaPlugin {
	private static VoteReward instance = null;

	private BagManager bagManager = null;

	public static VoteReward getInstance() {
		return VoteReward.instance;
	}

	public static void sendMessageToConsole(String message) {
		String str = VoteReward.instance.getConfig().getString("message.consoleSuffix") + " " + ChatColor.RESET + message;
		Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
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
			for (String key : achievableRewardFile.getKeys(false)) {
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
		} catch (IOException | InvalidConfigurationException ignored) {
		}
	}

	@Override
	public void onDisable() {
		this.bagManager.saveBags();
		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.stopMessage"));
	}

	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		VoteReward.instance = this;

		this.bagManager = new BagManager(this);

		CommandExecutor commandManager = new CommandManager(this.bagManager);
		Objects.requireNonNull(this.getCommand("votereward")).setExecutor(commandManager);

		Listener eventListener = new EventListener(this.bagManager);
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
		this.bagManager.loadBags();

		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.reloadComplete"));
	}

	public BagManager getBagManager() {
		return this.bagManager;
	}

	@Override
	public @NotNull String toString() {
		return "VoteReward{" +
				"bagManager=" + this.bagManager +
				"}";
	}
}

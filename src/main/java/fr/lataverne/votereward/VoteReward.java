package fr.lataverne.votereward;

import fr.lataverne.votereward.commands.VoteRewardCommand;
import fr.lataverne.votereward.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@SuppressWarnings ("ClassNamePrefixedWithPackageName")
public class VoteReward extends JavaPlugin {
	private static VoteReward instance = null;

	private BagManager bagManager = null;

	private GuiManager guiManager = null;

	private RewardGroupManager rewardGroupManager = null;

	private CommandsManager commandsManager = null;

	public static VoteReward getInstance() {
		return VoteReward.instance;
	}

	public static void sendMessageToConsole(String message) {
		String str = VoteReward.instance.getConfig().getString("message.consoleSuffix") + " " + ChatColor.RESET + message;
		Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
	}

	@Override
	public void onDisable() {
		this.rewardGroupManager.saveRewardGroups();
		this.bagManager.saveBags();

		this.commandsManager.unregisterCommands();

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
		this.guiManager = new GuiManager();
		this.rewardGroupManager = new RewardGroupManager();
		this.commandsManager = new CommandsManager();

		new VoteRewardCommand();

		Listener eventListener = new EventListener(this);
		Listener votifierManager = new VotifierManager(this.bagManager, this.rewardGroupManager);
		Bukkit.getPluginManager().registerEvents(eventListener, this);
		Bukkit.getPluginManager().registerEvents(votifierManager, this);

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

		this.rewardGroupManager.loadRewardGroups();
		this.bagManager.loadBags();

		VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.reloadComplete"));
	}

	public BagManager getBagManager() {
		return this.bagManager;
	}

	public GuiManager getGuiManager() {
		return this.guiManager;
	}

	public CommandsManager getCommandsManager() {
		return this.commandsManager;
	}

	@Override
	public @NotNull String toString() {
		return "VoteReward{" +
				"bagManager=" + this.bagManager +
				", guiManager=" + this.guiManager +
				", rewardGroupManager=" + this.rewardGroupManager +
				", commandsManager=" + this.commandsManager +
				"}";
	}
}

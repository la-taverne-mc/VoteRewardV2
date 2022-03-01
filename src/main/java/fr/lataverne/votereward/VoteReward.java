package fr.lataverne.votereward;

import fr.lataverne.votereward.commands.VoteRewardCommand;
import fr.lataverne.votereward.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class VoteReward extends JavaPlugin {

    private static VoteReward instance = null;

    private BagManager bagManager = null;

    private CommandsManager commandsManager = null;

    private GuiManager guiManager = null;

    private RewardsGroupManager rewardsGroupManager = null;

    public static VoteReward getInstance() {
        return VoteReward.instance;
    }

    public static void sendMessageToConsole(String message) {
        String str = VoteReward.instance.getConfig().getString("message.consoleSuffix");
        str += " " + ChatColor.RESET + message;
        Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
    }

    public BagManager getBagManager() {
        return this.bagManager;
    }

    public CommandsManager getCommandsManager() {
        return this.commandsManager;
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    public RewardsGroupManager getRewardsGroupManager() {
        return this.rewardsGroupManager;
    }

    @Override
    public void onDisable() {
        this.rewardsGroupManager.saveRewardGroups();
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

        this.bagManager = new BagManager();
        this.guiManager = new GuiManager();
        this.rewardsGroupManager = new RewardsGroupManager();
        this.commandsManager = new CommandsManager();

        new VoteRewardCommand();

        Listener eventListener = new EventListener(this);
        Listener votifierManager = new VotifierManager(this.bagManager, this.rewardsGroupManager);
        Bukkit.getPluginManager().registerEvents(eventListener, this);
        Bukkit.getPluginManager().registerEvents(votifierManager, this);

        InternalPermission.loadingInternalPermissions();

        VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.startMessage"));
    }

    /**
     * Method for reload the plugin's config. If the config don't exist then we save the default config.
     */
    @Override
    public void reloadConfig() {
        boolean configExist = true;
        if (!new File("plugins/VoteReward/config.yml").exists()) {
            this.saveDefaultConfig();
            configExist = false;
        }

        super.reloadConfig();

        VoteReward.sendMessageToConsole(configExist
                                        ? this.getConfig().getString("message.system.existingConfig")
                                        : this.getConfig().getString("message.system.nonExistingConfig"));

        this.rewardsGroupManager.loadRewardGroups();
        this.bagManager.loadBags();

        VoteReward.sendMessageToConsole(this.getConfig().getString("message.system.reloadComplete"));
    }

    @Override
    public @NotNull String toString() {
        return "VoteReward{" + "bagManager=" + this.bagManager + ", guiManager=" + this.guiManager +
               ", rewardGroupManager=" + this.rewardsGroupManager + ", commandsManager=" + this.commandsManager + "}";
    }
}

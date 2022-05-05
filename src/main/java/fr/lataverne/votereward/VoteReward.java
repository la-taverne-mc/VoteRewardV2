package fr.lataverne.votereward;

import fr.lataverne.votereward.commands.VoteRewardCommand;
import fr.lataverne.votereward.commands.VoteRewardConsoleCommand;
import fr.lataverne.votereward.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class VoteReward extends JavaPlugin {

    private static final String consoleSuffix = ChatColor.GREEN + "[" + ChatColor.WHITE + "VR" + ChatColor.GREEN + "]";

    private static VoteReward instance = null;

    private BagManager bagManager = null;

    private ChatResponseManager chatResponseManager = null;

    private CommandsManager commandsManager = null;

    private GuiManager guiManager = null;

    private RewardsGroupManager rewardsGroupManager = null;

    private VotingUserManager votingUserManager = null;

    public static VoteReward getInstance() {
        return instance;
    }

    public static void sendMessageToConsole(String message) {
        String str = consoleSuffix + " " + ChatColor.RESET + message;
        Bukkit.getConsoleSender().sendMessage(Helper.colorizeString(str));
    }

    public BagManager getBagManager() {
        return this.bagManager;
    }

    public ChatResponseManager getChatResponseManager() {
        return this.chatResponseManager;
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

    public VotingUserManager getVotingUserManager() {
        return this.votingUserManager;
    }

    @Override
    public void onDisable() {
        this.votingUserManager.saveVotingUsers();
        this.rewardsGroupManager.saveRewardGroups();
        this.bagManager.saveBags();

        this.commandsManager.unregisterCommands();

        sendMessageToConsole(ChatColor.RED + "VoteReward disabled");
    }

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        instance = this;

        this.votingUserManager = new VotingUserManager();
        this.bagManager = new BagManager();
        this.guiManager = new GuiManager();
        this.rewardsGroupManager = new RewardsGroupManager();
        this.commandsManager = new CommandsManager();
        this.chatResponseManager = new ChatResponseManager(this);

        new VoteRewardCommand();
        new VoteRewardConsoleCommand();

        Listener eventListener = new EventListener(this);
        Bukkit.getPluginManager().registerEvents(eventListener, this);

        sendMessageToConsole(ChatColor.GREEN + "VoteReward enabled");
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

        sendMessageToConsole(configExist
                             ? ChatColor.GREEN + "Config file found"
                             : ChatColor.RED + "Config file not found");

        this.votingUserManager.loadVotingUsers();
        this.rewardsGroupManager.loadRewardGroups();
        this.bagManager.loadBags();

        sendMessageToConsole(ChatColor.GREEN + "Reload complete");
    }
}

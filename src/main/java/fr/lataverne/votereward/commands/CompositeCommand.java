package fr.lataverne.votereward.commands;

import fr.lataverne.votereward.VoteReward;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CompositeCommand extends Command {

    private static final String COMMANDS = "commands.";
    @SuppressWarnings ("FieldNotUsedInToString")
    protected final @Nullable CompositeCommand parent;
    @SuppressWarnings ("FieldNotUsedInToString")
    protected final VoteReward plugin;
    private final String configPath;
    private final Map<String, CompositeCommand> subCommands = new LinkedHashMap<>();
    private final Map<String, CompositeCommand> subCommandAliases = new LinkedHashMap<>();
    private final int level;
    private final String topLabel;
    private boolean onlyPlayer = false;

    protected CompositeCommand(String label, String... aliases) {
        super(label, "", "", Arrays.asList(aliases));

        this.plugin = VoteReward.getInstance();
        this.topLabel = label;
        this.parent = null;
        this.level = 0;
        this.configPath = label;

        this.setAliases(new ArrayList<>(Arrays.asList(aliases)));

        this.setDescription(this.configPath + ".description");
        this.setUsage(this.configPath + ".usage");
        this.setup();

        if (this.getSubCommand("help") == null && !"help".equals(label)) {
            new HelpCommand(this);
        }

        if (this.plugin.getCommand(label) == null) {
            this.plugin.getCommandsManager().registerCommand(this);
        }
    }

    protected CompositeCommand(@NotNull CompositeCommand parent, String label, String... aliases) {
        super(label, "", "", Arrays.asList(aliases));

        this.plugin = VoteReward.getInstance();
        this.topLabel = parent.topLabel;
        this.parent = parent;
        this.level = parent.level + 1;
        this.onlyPlayer = parent.onlyPlayer;

        this.configPath = "help".equalsIgnoreCase(label) ? "help" : this.parent.configPath + "." + label;

        this.parent.subCommands.put(label.toLowerCase(Locale.ENGLISH), this);
        this.setAliases(new ArrayList<>(Arrays.asList(aliases)));

        for (String alias : aliases) {
            this.parent.subCommandAliases.put(alias.toLowerCase(Locale.ENGLISH), this);
        }

        this.setDescription(this.configPath + ".description");
        this.setUsage(this.configPath + ".usage");
        this.setup();

        if (this.getSubCommand("help") == null && !"help".equals(label)) {
            new HelpCommand(this);
        }
    }

    @Override
    public final @NotNull String getDescription() {
        return Objects.requireNonNullElseGet(this.plugin.getConfig().getString(CompositeCommand.COMMANDS + this.description), () -> CompositeCommand.COMMANDS + this.description);
    }

    @Override
    public @NotNull String getUsage() {
        return Objects.requireNonNullElseGet(this.plugin.getConfig().getString(CompositeCommand.COMMANDS + this.usageMessage), () -> CompositeCommand.COMMANDS + this.usageMessage);
    }

    protected final @Nullable String getParameters() {
        return this.plugin.getConfig().getString(CompositeCommand.COMMANDS + this.configPath + ".parameters");
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        CompositeCommand command = this.getCommandFromArgs(args);
        String cmdLabel = (command.level > 0) ? args[command.level - 1] : commandLabel;
        List<String> cmdArgs = Arrays.asList(args).subList(command.level, args.length);

        return command.call(sender, cmdLabel, cmdArgs);
    }

    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return this.showHelp(sender);
    }

    protected final boolean call(CommandSender sender, String label, List<String> args) {
        return this.canExecute(sender, true) && this.execute(sender, label, args);
    }

    protected boolean canExecute(CommandSender sender, boolean sendMessage) {
        if (this.onlyPlayer && !(sender instanceof Player)) {
            if (sendMessage) {
                sender.sendMessage(this.plugin.getConfig().getString("messages.error.use-only-in-game"));
            }
            return false;
        }

        if (sender.isOp() || this.getPermission() == null || sender.hasPermission(this.getPermission())) {
            return true;
        } else {
            if (sendMessage) {
                sender.sendMessage(this.plugin.getConfig().getString("messages.error.no-permission").replace("[permission]", this.getPermission()));
            }
            return false;
        }

    }

    @Contract (pure = true)
    public final @NotNull Collection<CompositeCommand> getSubCommands() {
        return this.subCommands.values();
    }

    protected abstract void setup();

    public final void inheritPermission() {
        this.setPermission(this.parent.getPermission());
    }

    protected final @Nullable CompositeCommand getSubCommand(@NotNull String label) {
        String lowerLabel = label.toLowerCase(Locale.ENGLISH);

        CompositeCommand subCommand = this.subCommands.getOrDefault(lowerLabel, null);

        if (subCommand != null) {
            return subCommand;
        }

        return this.subCommandAliases.getOrDefault(lowerLabel, null);
    }

    private boolean hasSubCommands() {
        return !this.subCommands.isEmpty();
    }

    private @NotNull CompositeCommand getCommandFromArgs(String @NotNull [] args) {
        CompositeCommand command = this;

        for (String arg : args) {
            if (command.hasSubCommands()) {
                CompositeCommand subCommand = command.getSubCommand(arg);

                if (subCommand == null) {
                    return command;
                } else {
                    command = subCommand;
                }
            } else {
                return command;
            }
        }

        return command;
    }

    public boolean isOnlyPlayer() {
        return this.onlyPlayer;
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    public void misuseCommand(@NotNull CommandSender sender) {
        String parameters = this.getParameters();

        sender.sendMessage(this.plugin.getConfig().getString("messages.error.misuse-command"));
        sender.sendMessage(ChatColor.RED + this.plugin.getConfig().getString(CompositeCommand.COMMANDS + this.usageMessage + (parameters != null ? " " + parameters : "")));
    }

    public boolean showHelp(CommandSender sender) {
        CompositeCommand helpCommand = this.getSubCommand("help");
        if (helpCommand != null) {
            return helpCommand.call(sender, "help", new ArrayList<>());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "CompositeCommand{" +
                "configPath='" + this.configPath + "'" +
                ", label='" + this.getLabel() + "'" +
                ", subCommands=" + this.subCommands +
                ", subCommandAliases=" + this.subCommandAliases +
                ", permission='" + this.getPermission() + "'" +
                ", level=" + this.level +
                ", topLabel='" + this.topLabel + "'" +
                ", description='" + this.description + "'" +
                ", usageMessage='" + this.usageMessage + "'" +
                ", onlyPlayer=" + this.onlyPlayer + "'" +
                "}";
    }
}
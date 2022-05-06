package fr.lataverne.votereward.utils.commands;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.commands.common.ConfirmCommand;
import fr.lataverne.votereward.commands.common.HelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CompositeCommand extends Command {

    protected static final String COMMANDS = "commands.";

    protected final int level;

    protected final @Nullable CompositeCommand parent;

    protected final VoteReward plugin;

    private final String configPath;

    private final Collection<Parameter> parameters = new ArrayList<>();

    private final Map<String, CompositeCommand> subCommandAliases = new LinkedHashMap<>();

    private final Map<String, CompositeCommand> subCommands = new LinkedHashMap<>();

    private final String topLabel;

    private boolean hidden = false;

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
        this.setPermission();
        this.setup();

        if (this.getSubCommand("help") == null && !"help".equals(label)) {
            this.addChildren(new HelpCommand(this));
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

        this.configPath = "help".equalsIgnoreCase(label)
                          ? "help"
                          : this.parent.configPath + "." + label;

        this.parent.subCommands.put(label.toLowerCase(Locale.ENGLISH), this);
        this.setAliases(new ArrayList<>(Arrays.asList(aliases)));

        for (String alias : aliases) {
            this.parent.subCommandAliases.put(alias.toLowerCase(Locale.ENGLISH), this);
        }

        this.setDescription(this.configPath + ".description");
        this.setUsage(this.configPath + ".usage");
        this.setPermission();
        this.setup();

        if (this.getSubCommand("help") == null && !"help".equals(label)) {
            this.addChildren(new HelpCommand(this));
        }
    }

    public boolean canExecute(CommandSender sender, boolean sendMessage) {
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
                sender.sendMessage(this.plugin.getConfig()
                                              .getString("messages.error.no-permission")
                                              .replace("[permission]", this.getPermission()));
            }
            return false;
        }
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        CompositeCommand command = this.getCommandFromArgs(args);
        String cmdLabel = (command.level > 0)
                          ? args[command.level - 1]
                          : commandLabel;

        return command.call(sender, cmdLabel, List.of(args));
    }

    public @Nullable ConfirmCommand getConfirmCommand() {
        CompositeCommand command = this.getSubCommand("confirm");
        if (command == null) {
            if (this.parent != null) {
                return this.parent.getConfirmCommand();
            }
        } else {
            if (command instanceof ConfirmCommand confirmCommand) {
                return confirmCommand;
            }
        }

        return null;
    }

    @Override
    public final @NotNull String getDescription() {
        return Objects.requireNonNullElseGet(this.plugin.getConfig().getString(COMMANDS + this.description), () ->
                COMMANDS + this.description);
    }

    public final @Nullable String getParameters() {
        return this.plugin.getConfig().getString(COMMANDS + this.configPath + ".parameters");
    }

    @Contract(pure = true)
    public final @NotNull Collection<CompositeCommand> getSubCommands() {
        return this.subCommands.values().stream().filter(compositeCommand -> !compositeCommand.hidden).toList();
    }

    @Override
    public @NotNull String getUsage() {
        return Objects.requireNonNullElseGet(this.plugin.getConfig().getString(COMMANDS + this.usageMessage), () ->
                COMMANDS + this.usageMessage);
    }

    public boolean hasSubCommands() {
        return !this.subCommands.keySet().stream().filter(label -> !"help".equals(label)).toList().isEmpty();
    }

    public final void inheritPermission() {
        this.setPermission(this.parent.getPermission());
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public final void misuseCommand(@NotNull CommandSender sender) {
        String usage = this.getUsage();
        String params = this.getParameters();

        sender.sendMessage(this.plugin.getConfig().getString("messages.error.misuse-command"));
        sender.sendMessage(ChatColor.RED + usage + (params != null
                                                    ? " " + params
                                                    : ""));
        sender.sendMessage(ChatColor.RED + this.plugin.getConfig().getString("messages.tips.help-command"));
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    public void setPermission() {
        super.setPermission(this.parent == null
                            ? this.getLabel()
                            : this.parent.getPermission() + "." + this.getLabel());
    }

    public boolean showHelp(CommandSender sender) {
        CompositeCommand helpCommand = this.getSubCommand("help");
        return helpCommand != null && helpCommand.call(sender, "help", new ArrayList<>());
    }

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        CompositeCommand command = this.getCommandFromArgs(args);
        String cmdLabel = (command.level > 0)
                          ? args[command.level - 1]
                          : alias;
        List<String> cmdArgs = Arrays.asList(args).subList(command.level, args.length);

        if ((!command.onlyPlayer || sender instanceof Player) && command.canExecute(sender, false)) {
            options.addAll(command.tabComplete(sender, cmdLabel, Arrays.asList(args)));

            if (command.hasSubCommands() && cmdArgs.size() < 2) {
                command.getSubCommands()
                       .stream()
                       .filter(subCommand -> subCommand.canExecute(sender, false))
                       .filter(subCommand -> !(subCommand instanceof DynamicCommand))
                       .map(Command::getLabel)
                       .forEach(options::add);
            }

            String lastArg = args.length == 0
                             ? ""
                             : args[args.length - 1];

            return options.stream()
                          .filter(s -> s != null &&
                                       s.toLowerCase(Locale.ENGLISH).startsWith(lastArg.toLowerCase(Locale.ENGLISH)))
                          .sorted()
                          .toList();
        } else {
            return options;
        }
    }

    protected void addChildren(@NotNull CompositeCommand subCommand) {
        this.parameters.add(new Parameter(subCommand.getLabel(), false));
    }

    protected void addChildren(String parameter, boolean isOptional) {
        this.parameters.add(new Parameter(parameter, isOptional));
    }

    protected final boolean call(CommandSender sender, String label, List<String> args) {
        return this.canExecute(sender, true) && this.execute(sender, label, args);
    }

    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        this.misuseCommand(sender);
        return true;
    }

    protected @Nullable CompositeCommand getSubCommand(@NotNull String label) {
        String lowerLabel = label.toLowerCase(Locale.ENGLISH);

        CompositeCommand subCommand = this.subCommands.getOrDefault(lowerLabel, null);

        if (subCommand == null) {
            subCommand = this.subCommandAliases.getOrDefault(lowerLabel, null);

            if (subCommand == null) {
                Optional<DynamicCommand> dynamicCommand = this.subCommands.values()
                                                                          .stream()
                                                                          .filter(cmd -> cmd instanceof DynamicCommand)
                                                                          .map(cmd -> (DynamicCommand) cmd)
                                                                          .filter(cmd -> cmd.isDynamicCommand(lowerLabel))
                                                                          .findFirst();

                if (dynamicCommand.isPresent()) {
                    subCommand = dynamicCommand.get();
                }
            }
        }

        return subCommand;
    }

    protected abstract void setup();

    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return new ArrayList<>();
    }

    private @NotNull CompositeCommand getCommandFromArgs(String @NotNull [] args) {
        CompositeCommand command = this;

        for (String arg : args) {
            CompositeCommand subCommand = command.getSubCommand(arg);

            if (subCommand == null) {
                return command;
            } else {
                command = subCommand;
            }
        }

        return command;
    }
}

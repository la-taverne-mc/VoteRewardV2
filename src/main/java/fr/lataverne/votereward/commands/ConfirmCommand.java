package fr.lataverne.votereward.commands;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmCommand extends CompositeCommand {

    private final Map<CommandSender, Pair<BukkitTask, Runnable>> toBeConfirmed = new HashMap<>();

    protected ConfirmCommand(@NotNull CompositeCommand parent) {
        super(parent, "confirm");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (!cmdArgs.isEmpty()) {
            this.misuseCommand(sender);
            return true;
        }

        if (!this.toBeConfirmed.containsKey(sender)) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.confirm-command.no-command-to-confirm"));
            return true;
        }

        this.toBeConfirmed.get(sender).getLeft().cancel();
        Bukkit.getScheduler().runTask(this.plugin, this.toBeConfirmed.get(sender).getRight());
        this.toBeConfirmed.remove(sender);

        return true;
    }

    public void addCommandToBeConfirmed(ConfirmableCommand command, CommandSender sender, String label, List<String> args) {
        if (this.toBeConfirmed.containsKey(sender)) {
            this.toBeConfirmed.get(sender).getLeft().cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            sender.sendMessage(this.plugin.getConfig().getString("messages.confirm-command.request-cancelled"));
            this.toBeConfirmed.remove(sender);
        }, 200);

        this.toBeConfirmed.put(sender, new ImmutablePair<>(task, () -> command.toBeExecuted(sender, label, args)));
    }

    @Override
    protected void setup() {
        this.inheritPermission();
    }
}

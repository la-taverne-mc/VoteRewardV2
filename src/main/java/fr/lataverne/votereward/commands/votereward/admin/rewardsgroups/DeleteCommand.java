package fr.lataverne.votereward.commands.admin.rewardsgroups;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.ConfirmableCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteRewardsGroupAdminCommand extends ConfirmableCommand {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    public DeleteRewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "delete");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups.delete");
    }

    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        return cmdArgs.isEmpty() || cmdArgs.size() == 1
               ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
               : new ArrayList<>();
    }

    @Override
    protected boolean toBeExecuted(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());

        String rewardsGroupName = cmdArgs.get(0);
        boolean success = this.plugin.getRewardsGroupManager().deleteRewardsGroup(rewardsGroupName);

        String message = success
                         ? this.plugin.getConfig()
                                      .getString("messages.admin.rewards-group.deleted")
                                      .replace(REWARDS_GROUP_NAME, rewardsGroupName)
                         : this.plugin.getConfig()
                                      .getString("messages.admin.rewards-group.unable-to-delete")
                                      .replace(REWARDS_GROUP_NAME, rewardsGroupName);

        sender.sendMessage(message);

        return true;
    }

    @Override
    protected boolean validate(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() != 1) {
            this.misuseCommand(sender);
            return false;
        }

        String rewardsGroupName = cmdArgs.get(0);
        if (this.plugin.getRewardsGroupManager().getRewardGroup(rewardsGroupName) == null) {
            String message = this.plugin.getConfig()
                                        .getString("messages.admin.rewards-group.unknown-rewards-group")
                                        .replace(REWARDS_GROUP_NAME, rewardsGroupName);
            sender.sendMessage(message);
            return false;
        }

        return true;
    }
}

package fr.lataverne.votereward.commands.admin.rewardsgroup.reward;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.commands.admin.rewardsgroup.reward.modify.DynRewardListAdminCommand;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModifyAdminCommand extends CompositeCommand {

    public ModifyAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "modify");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup.<rewards-group-name>.reward.modify");

        new DynRewardListAdminCommand(this);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        RewardsGroup rewardsGroup = this.plugin.getRewardsGroupManager().getRewardGroup(args.get(this.level - 2));

        List<String> cmdArgs = args.subList(this.level, args.size());
        return cmdArgs.isEmpty() || cmdArgs.size() == 1
               ? rewardsGroup.getAchievableRewardsAndIds()
                             .stream()
                             .map(entry -> Integer.toString(entry.getKey().intValue()))
                             .toList()
               : new ArrayList<>();
    }
}

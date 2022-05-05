package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards;

import fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.rewards.modify.DynRewardListCommand;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModifyCommand extends CompositeCommand {

    public ModifyCommand(@NotNull CompositeCommand parent) {
        super(parent, "modify");
    }

    @Override
    protected void setup() {
        this.addChildren(new DynRewardListCommand(this));
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

package fr.lataverne.votereward.commands.votereward.admin;

import fr.lataverne.votereward.commands.votereward.admin.rewardsgroup.DynRewardsGroupCommand;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardsGroupCommand extends CompositeCommand {

    public RewardsGroupCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewardsgroup", "rgroup");
    }

    @Override
    protected void setup() {
        this.addChildren(new DynRewardsGroupCommand(this));
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
               ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
               : new ArrayList<>();
    }
}

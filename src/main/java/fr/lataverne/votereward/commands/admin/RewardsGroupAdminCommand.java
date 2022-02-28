package fr.lataverne.votereward.commands.admin;

import fr.lataverne.votereward.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardsGroupAdminCommand extends CompositeCommand {

    public RewardsGroupAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "rewardsgroup", "rgroup");
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroup");

        new DynRewardsGroupAdminCommand(this);
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
               ? this.plugin.getRewardsGroupManager().getRewardsGroups().keySet().stream().toList()
               : new ArrayList<>();
    }
}

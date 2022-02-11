package fr.lataverne.votereward.commands.admin.rewardsgroups.rewardsgroup;

import fr.lataverne.votereward.commands.CompositeCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddRewardAdminCommand extends CompositeCommand {
    public AddRewardAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "addreward", "add");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() > 1) {
            this.misuseCommand(sender);
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(label);

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.admin.rewards-group.add-reward.nothing-in-main-hand"));
            return true;
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups.<rewards-group-name>.addreward");
        this.setOnlyPlayer(true);
    }
}

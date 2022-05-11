package fr.lataverne.votereward.commands.votereward.admin.rewardsgroup;

import fr.lataverne.votereward.gui.admin.RewardsGroupView;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeeCommand extends CompositeCommand {

    public SeeCommand(@NotNull CompositeCommand parent) {
        super(parent, "see");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() > 1) {
            this.misuseCommand(sender);
            return true;
        }

        int page = 0;

        if (cmdArgs.size() == 1) {
            if (NumberUtils.isDigits(cmdArgs.get(0))) {
                page = NumberUtils.toInt(cmdArgs.get(0));
            } else {
                this.misuseCommand(sender);
                return true;
            }
        }

        Player player = (Player) sender;

        RewardsGroup rewardsGroup = this.plugin.getRewardsGroupManager().getRewardGroup(args.get(this.level - 2));
        RewardsGroupView view = this.plugin.getGuiManager().getRewardsGroupView(player, rewardsGroup, page);

        player.openInventory(view.getInventory());

        return true;
    }

    @Override
    protected void setup() {
        this.setOnlyPlayer(true);
    }
}

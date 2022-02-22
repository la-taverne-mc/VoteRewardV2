package fr.lataverne.votereward.commands.bag;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.managers.BagManager;
import fr.lataverne.votereward.objects.Bag;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetBagCommand extends CompositeCommand {

    public GetBagCommand(@NotNull CompositeCommand parent) {
        super(parent, "get");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs != null && cmdArgs.size() > 2) {
            this.misuseCommand(sender);
            return true;
        }

        Player player = (Player) sender;

        int maxNbRewardsRetrieving = Constant.MAX_NB_REWARDS_RETRIEVING;

        if (cmdArgs.size() == 1) {
            if (NumberUtils.isDigits(cmdArgs.get(0))) {
                maxNbRewardsRetrieving = NumberUtils.toInt(cmdArgs.get(0));
            } else {
                this.misuseCommand(sender);
                return true;
            }
        }

        Bag bag = this.plugin.getBagManager().getOrCreateBag(player.getUniqueId());
        BagManager.giveBag(bag, player, maxNbRewardsRetrieving);

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.bag.get");
    }
}

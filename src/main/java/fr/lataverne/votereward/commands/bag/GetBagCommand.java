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
        if (args != null && args.size() > 2) {
            this.misuseCommand(sender);
            return true;
        }

        Player player = (Player) sender;

        int maxNbRewardsRetrieving = Constant.MAX_NB_REWARDS_RETRIEVING;

        if (args.size() == 1) {
            if (NumberUtils.isDigits(args.get(0))) {
                maxNbRewardsRetrieving = NumberUtils.toInt(args.get(0));
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

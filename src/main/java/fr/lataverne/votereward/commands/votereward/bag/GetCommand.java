package fr.lataverne.votereward.commands.votereward.bag;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.managers.BagManager;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetCommand extends CompositeCommand {

    public GetCommand(@NotNull CompositeCommand parent) {
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
    }
}

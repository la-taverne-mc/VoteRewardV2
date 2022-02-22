package fr.lataverne.votereward.commands.admin.bag;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.managers.BagManager;
import fr.lataverne.votereward.objects.Bag;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GetBagAdminCommand extends CompositeCommand {

    private static final String PLAYER = "[player]";

    public GetBagAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "get");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.isEmpty() || cmdArgs.size() > 2) {
            this.misuseCommand(sender);
        } else {
            @SuppressWarnings ("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(cmdArgs.get(0));

            if (!player.hasPlayedBefore()) {
                sender.sendMessage(this.plugin.getConfig().getString("messages.error.unknown-player").replace(GetBagAdminCommand.PLAYER, player.getName()));
                return true;
            }

            int maxNbRewardsRetrieving = Constant.MAX_NB_REWARDS_RETRIEVING;

            if (cmdArgs.size() == 2) {
                if (NumberUtils.isDigits(cmdArgs.get(1))) {
                    maxNbRewardsRetrieving = NumberUtils.toInt(cmdArgs.get(1));
                } else {
                    this.misuseCommand(sender);
                    return true;
                }
            }

            Bag bag = this.plugin.getBagManager().getOrCreateBag(player.getUniqueId());
            BagManager.giveBag(bag, (Player) sender, maxNbRewardsRetrieving);
        }

        return true;
    }

    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        return args.isEmpty() || args.size() == 1
                ? this.plugin.getBagManager().getOwnerNames()
                : new ArrayList<>();
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.bag.get");
    }
}

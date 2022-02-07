package fr.lataverne.votereward.commands.admin.bag;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.gui.BagView;
import fr.lataverne.votereward.objects.Bag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeeBagAdminCommand extends CompositeCommand {

    private static final String PLAYER = "[player]";

    public SeeBagAdminCommand(@NotNull CompositeCommand parent) {
        super(parent, "see");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        if (args.size() != 1) {
            this.misuseCommand(sender);
            return true;
        }

        @SuppressWarnings ("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));

        if (!player.hasPlayedBefore()) {
            sender.sendMessage(this.plugin.getConfig().getString("messages.error.unknown-player").replace(SeeBagAdminCommand.PLAYER, player.getName()));
            return true;
        }

        Player senderPlayer = (Player) sender;

        Bag bag = this.plugin.getBagManager().getOrCreateBag(player.getUniqueId());
        BagView bagView = this.plugin.getGuiManager().getBagView(senderPlayer, bag);

        senderPlayer.openInventory(bagView.getInventory());

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.bag.see");
    }
}

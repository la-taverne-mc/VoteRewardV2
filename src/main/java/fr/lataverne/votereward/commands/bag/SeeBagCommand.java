package fr.lataverne.votereward.commands.bag;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.gui.BagView;
import fr.lataverne.votereward.objects.Bag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeeBagCommand extends CompositeCommand {

    public SeeBagCommand(@NotNull CompositeCommand parent) {
        super(parent, "see");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs != null && !cmdArgs.isEmpty()) {
            this.misuseCommand(sender);
            return true;
        }

        Player player = (Player) sender;

        Bag bag = this.plugin.getBagManager().getOrCreateBag(player.getUniqueId());
        BagView bagView = this.plugin.getGuiManager().getBagView(player, bag);

        player.openInventory(bagView.getInventory());

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.bag.see");
    }
}

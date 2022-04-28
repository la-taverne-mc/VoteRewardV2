package fr.lataverne.votereward.commands.votereward.admin.rewardsgroups;

import fr.lataverne.votereward.gui.admin.RewardsGroupListAdminView;
import fr.lataverne.votereward.utils.commands.CompositeCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ListCommand extends CompositeCommand {

    public static final String HEADER_LIST =
            ChatColor.GREEN + "========== " + ChatColor.DARK_GREEN + "REWARDS GROUPS" + ChatColor.GREEN + " ==========";

    public ListCommand(@NotNull CompositeCommand parent) {
        super(parent, "list");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
        List<String> cmdArgs = args.subList(this.level, args.size());
        if (cmdArgs.size() > 2) {
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

        Set<String> rewardsGroups = this.plugin.getRewardsGroupManager().getRewardsGroups().keySet();

        if (sender instanceof Player player) {
            RewardsGroupListAdminView view = this.plugin.getGuiManager().getRewardsGroupListView(player, page);
            player.openInventory(view.getInventory());
        } else {
            String enabledRewardsGroup = this.plugin.getRewardsGroupManager().getEnabledRewardsGroupName();

            sender.sendMessage(HEADER_LIST);

            for (String rewardsGroupName : rewardsGroups) {
                StringBuilder message = new StringBuilder(rewardsGroupName);

                message.insert(0, rewardsGroupName.equals(enabledRewardsGroup)
                                  ? ChatColor.RED + "" + ChatColor.BOLD + "> "
                                  : ChatColor.YELLOW + "  ");

                sender.sendMessage(message.toString());
            }
        }

        return true;
    }

    @Override
    protected void setup() {
    }
}

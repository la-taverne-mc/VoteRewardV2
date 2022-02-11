package fr.lataverne.votereward.commands.admin.rewardsgroups;

import fr.lataverne.votereward.commands.CompositeCommand;
import fr.lataverne.votereward.gui.admin.RewardsGroupListAdminView;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class RewardsGroupListAdminCommand extends CompositeCommand {

    public static final String HEADER_LIST = ChatColor.GREEN + "========== " + ChatColor.DARK_GREEN + "REWARDS GROUPS" + ChatColor.GREEN + " ==========";

    public RewardsGroupListAdminCommand(@NotNull CompositeCommand parent) {
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

            sender.sendMessage(RewardsGroupListAdminCommand.HEADER_LIST);

            for (String rewardsGroupName : rewardsGroups) {
                StringBuilder message = new StringBuilder(rewardsGroupName);

                if (rewardsGroupName.equals(enabledRewardsGroup)) {
                    message.insert(0, ChatColor.RED + "" + ChatColor.BOLD + "> ");
                } else {
                    message.insert(0, ChatColor.YELLOW + "  ");
                }

                sender.sendMessage(message.toString());
            }
        }

        return true;
    }

    @Override
    protected void setup() {
        this.setPermission("votereward.admin.rewardsgroups.list");
    }
}

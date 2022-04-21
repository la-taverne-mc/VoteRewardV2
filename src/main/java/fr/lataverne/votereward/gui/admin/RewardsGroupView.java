package fr.lataverne.votereward.gui.admin;

import fr.lataverne.votereward.gui.ConfirmView;
import fr.lataverne.votereward.gui.EInventoryAction;
import fr.lataverne.votereward.gui.Gui;
import fr.lataverne.votereward.gui.NavigableGui;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.utils.StringParameterizedRunnable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RewardsGroupView extends NavigableGui {

    private static final String NB_REWARDS = "[nb-rewards]";

    private static final String PERCENTAGE = "[percentage]";

    private static final String REAL_PERCENTAGE = "[real-percentage]";

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    private final RewardsGroup rewardsGroup;

    public RewardsGroupView(RewardsGroup rewardsGroup, int page) {
        super(54, page);

        this.rewardsGroup = rewardsGroup;
    }

    @Override
    public @NotNull String getTitle() {
        String rewardsGroupName = this.plugin.getRewardsGroupManager().getRewardsGroupName(this.rewardsGroup);
        if (rewardsGroupName == null) {
            rewardsGroupName = "Unknown rewards group";
        }

        return this.plugin.getConfig()
                          .getString("gui.admin.rewards-group-view.title")
                          .replace(REWARDS_GROUP_NAME, rewardsGroupName);
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() >= 0 && event.getRawSlot() <= 44) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                int id = getIdOfItemClicked(clickedItem);
                Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

                if (id != -1) {
                    switch (event.getClick()) {
                        case RIGHT -> this.rightClickOnItem(player, id);
                        case SHIFT_RIGHT -> this.shiftRightClickOnItem(player, id);
                    }
                }
            }

            return EInventoryAction.Nothing;
        } else {
            return super.onInventoryClickEvent(event);
        }
    }

    @Override
    protected void setContent() {
        this.setHeader();

        List<Map.Entry<Integer, AchievableReward>> achievableRewards = this.getAchievableRewardsToBeDisplayed();

        int nbAchievableRewards = achievableRewards.size();

        for (int i = 0; i < 36; i++) {
            if (i < nbAchievableRewards) {
                Map.Entry<Integer, AchievableReward> reward = achievableRewards.get(i);
                this.content[i + 9] = this.getAchievableRewardView(reward.getKey().intValue(), reward.getValue());
            } else {
                this.content[i + 9] = new ItemStack(Material.AIR);
            }
        }

        super.setContent();
    }

    private @NotNull ItemStack getAchievableRewardView(int id, @NotNull AchievableReward achievableReward) {
        ItemStack item = new ItemStack(achievableReward.getReward().getItem());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            String realPercentage = String.format(Locale.ENGLISH, "%.2f", this.rewardsGroup.getRealPercentageOfReward(achievableReward));

            lore.add(ChatColor.GRAY + ID_LINE + id);
            lore.add("");
            lore.add(this.plugin.getConfig()
                                .getString("gui.admin.rewards-group-view.reward-view.percentage")
                                .replace(PERCENTAGE, Double.toString(achievableReward.getPercentage())));
            lore.add(this.plugin.getConfig()
                                .getString("gui.admin.rewards-group-view.reward-view.real-percentage")
                                .replace(REAL_PERCENTAGE, realPercentage));
            lore.add("");
            lore.add(this.plugin.getConfig()
                                .getString("gui.admin.rewards-group-view.reward-view.shortcut-to-edit-percentage"));
            lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-view.reward-view.shortcut-to-remove"));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull List<Map.Entry<Integer, AchievableReward>> getAchievableRewardsToBeDisplayed() {
        if (this.rewardsGroup == null) {
            return new ArrayList<>();
        }

        List<Map.Entry<Integer, AchievableReward>> achievableRewardsWithId = this.rewardsGroup.getAchievableRewardsAndIds()
                                                                                              .stream()
                                                                                              .toList();

        Pair<Integer, Integer> indexes = this.getFirstAndLastIndexes(achievableRewardsWithId, 36);

        return indexes.getLeft().intValue() == -1 || indexes.getRight().intValue() == -1
               ? new ArrayList<>()
               : achievableRewardsWithId.subList(indexes.getLeft().intValue(), indexes.getRight().intValue());
    }

    private @NotNull ItemStack getRewardsGroupIcon() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null && this.rewardsGroup != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            lore.add("");
            lore.add(this.plugin.getConfig()
                                .getString("gui.admin.rewards-group-view.rewards-group-icon.nb-rewards")
                                .replace(NB_REWARDS, Integer.toString(this.rewardsGroup.getNbRewards())));

            meta.setLore(lore);

            meta.setDisplayName(
                    ChatColor.RESET + this.plugin.getRewardsGroupManager().getRewardsGroupName(this.rewardsGroup));

            item.setItemMeta(meta);
        }

        return item;
    }

    private void parsePercentageRunnable(@NotNull Player player, @NotNull AchievableReward reward, String runnableParam) {
        try {
            double percentage = Double.parseDouble(runnableParam);
            reward.setPercentage(percentage);

            RewardsGroupView view = this.plugin.getGuiManager()
                                               .getRewardsGroupView(player, this.rewardsGroup, this.page);
            player.openInventory(view.getInventory());
        } catch (NumberFormatException ignored) {
            player.sendMessage(this.plugin.getConfig()
                                          .getString("messages.admin.rewards-group.update-reward-percentage.failed"));
        }
    }

    private void rightClickOnItem(@NotNull Player player, int rewardId) {
        AchievableReward reward = this.rewardsGroup.getAchievableReward(rewardId);

        this.close();
        player.sendMessage(this.plugin.getConfig()
                                      .getString("messages.admin.rewards-group.update-reward-percentage.request"));

        Runnable runnable = new StringParameterizedRunnable() {

            @Override
            public void run() {
                RewardsGroupView.this.parsePercentageRunnable(player, reward, this.parameter);
            }
        };

        this.plugin.getChatResponseManager().add(player.getUniqueId(), runnable);
    }

    private void setHeader() {
        this.content[0] = Gui.getEmptySpace();
        this.content[1] = Gui.getEmptySpace();
        this.content[2] = Gui.getEmptySpace();
        this.content[3] = Gui.getEmptySpace();
        this.content[4] = this.getRewardsGroupIcon();
        this.content[5] = Gui.getEmptySpace();
        this.content[6] = Gui.getEmptySpace();
        this.content[7] = Gui.getEmptySpace();
        this.content[8] = Gui.getEmptySpace();
    }

    private void shiftRightClickOnItem(Player player, int id) {
        String rewardsGroupName = this.plugin.getRewardsGroupManager().getRewardsGroupName(this.rewardsGroup);
        String actionInfo = this.plugin.getConfig()
                                       .getString("gui.admin.rewards-group-view.action-info.remove-reward")
                                       .replace(REWARDS_GROUP_NAME, rewardsGroupName);

        this.close();

        ConfirmView confirmView = this.plugin.getGuiManager().getConfirmView(player, actionInfo, () -> {
            this.rewardsGroup.removeAchievableReward(id);
            RewardsGroupView rewardsGroupView = this.plugin.getGuiManager()
                                                           .getRewardsGroupView(player, this.rewardsGroup, this.page);
            player.openInventory(rewardsGroupView.getInventory());
        });

        player.openInventory(confirmView.getInventory());
    }
}

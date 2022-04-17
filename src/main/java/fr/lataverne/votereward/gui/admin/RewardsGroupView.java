package fr.lataverne.votereward.gui.admin;

import fr.lataverne.votereward.gui.ConfirmView;
import fr.lataverne.votereward.gui.EInventoryAction;
import fr.lataverne.votereward.gui.Gui;
import fr.lataverne.votereward.gui.NavigableGui;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RewardsGroupView extends NavigableGui {

    private static final String ID_LINE = "ID: ";

    private static final Pattern ID_REWARD_LORE_PATTERN = Pattern.compile("^(ID: \\d+)$");

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
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    int id = getIdOfAchievableRewardClicked(clickedItem);
                    if (id != -1) {
                        String rewardsGroupName = this.plugin.getRewardsGroupManager()
                                                             .getRewardsGroupName(this.rewardsGroup);
                        String actionInfo = this.plugin.getConfig()
                                                       .getString("gui.admin.rewards-group-view.action-info.remove-reward")
                                                       .replace(REWARDS_GROUP_NAME, rewardsGroupName);
                        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

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
            }

            return EInventoryAction.Nothing;
        } else {
            return super.onInventoryClickEvent(event);
        }
    }

    @Override
    public String toString() {
        return "RewardsGroupView{" + ", page=" + this.page + ", rewardsGroup=" + this.rewardsGroup + "}";
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

    private static int getIdOfAchievableRewardClicked(@NotNull ItemStack clickedItem) {
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                for (String line : lore) {
                    String str = ChatColor.stripColor(line);
                    if (ID_REWARD_LORE_PATTERN.matcher(str).matches()) {
                        try {
                            String strId = str.replace(ID_LINE, "");
                            return Integer.parseInt(strId);
                        } catch (NumberFormatException ignored) {
                            return -1;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private @NotNull ItemStack getAchievableRewardView(int id, @NotNull AchievableReward achievableReward) {
        ItemStack item = new ItemStack(achievableReward.reward().getItem());
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
                                .replace(PERCENTAGE, Double.toString(achievableReward.percentage())));
            lore.add(this.plugin.getConfig()
                                .getString("gui.admin.rewards-group-view.reward-view.real-percentage")
                                .replace(REAL_PERCENTAGE, realPercentage));
            lore.add("");
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
}

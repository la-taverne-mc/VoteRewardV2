package fr.lataverne.votereward.gui.admin;

import fr.lataverne.votereward.gui.EInventoryAction;
import fr.lataverne.votereward.gui.Gui;
import fr.lataverne.votereward.gui.NavigableGui;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RewardsGroupView extends NavigableGui {

    private static final String REWARDS_GROUP_NAME = "[rewards-group-name]";

    private static final String PERCENTAGE = "[percentage]";

    private static final String REAL_PERCENTAGE = "[real-percentage]";

    private static final String NB_REWARDS = "[nb-rewards]";

    private final RewardsGroup rewardsGroup;

    public RewardsGroupView(RewardsGroup rewardsGroup, int page) {
        super(54, page);

        this.rewardsGroup = rewardsGroup;
    }

    @Override
    protected void setContent() {
        this.setHeader();

        List<AchievableReward> achievableRewards = this.getAchievableRewardsToBeDisplayed();

        int size = achievableRewards.size();

        for (int i = 0; i < 36; i++) {
            this.content[i + 9] = i < size
                    ? this.getAchievableRewardView(achievableRewards.get(i))
                    : new ItemStack(Material.AIR);
        }

        super.setContent();
    }

    private @NotNull ItemStack getAchievableRewardView(@NotNull AchievableReward achievableReward) {
        ItemStack item = new ItemStack(achievableReward.reward());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            String realPercentage = String.format(Locale.ENGLISH, "%.2f", this.rewardsGroup.getRealPercentageOfReward(achievableReward));

            lore.add("");
            lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-view.reward-view.percentage").replace(RewardsGroupView.PERCENTAGE, Double.toString(achievableReward.percentage())));
            lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-view.reward-view.real-percentage").replace(RewardsGroupView.REAL_PERCENTAGE, realPercentage));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull List<AchievableReward> getAchievableRewardsToBeDisplayed() {
        if (this.rewardsGroup == null) {
            return new ArrayList<>();
        }

        List<AchievableReward> achievableRewards = this.rewardsGroup.getAchievableRewards().stream().toList();

        Pair<Integer, Integer> indexes = this.getFirstAndLastIndexes(achievableRewards, 36);

        return indexes.getLeft().intValue() == -1 || indexes.getRight().intValue() == -1
                ? new ArrayList<>()
                : achievableRewards.subList(indexes.getLeft().intValue(), indexes.getRight().intValue());
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() >= 0 && event.getRawSlot() <= 44) {
            event.setCancelled(true);
            return EInventoryAction.Nothing;
        } else {
            return super.onInventoryClickEvent(event);
        }
    }

    @Override
    public @NotNull String getTitle() {
        String rewardsGroupName = this.plugin.getRewardsGroupManager().getRewardsGroupName(this.rewardsGroup);
        if (rewardsGroupName == null) {
            rewardsGroupName = "Unknown rewards group";
        }

        return this.plugin.getConfig().getString("gui.admin.rewards-group-view.title").replace(RewardsGroupView.REWARDS_GROUP_NAME, rewardsGroupName);
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

    private @NotNull ItemStack getRewardsGroupIcon() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null && this.rewardsGroup != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            lore.add("");
            lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-view.rewards-group-icon.nb-rewards").replace(RewardsGroupView.NB_REWARDS, Integer.toString(this.rewardsGroup.getNbRewards())));

            meta.setLore(lore);

            meta.setDisplayName(ChatColor.RESET + this.plugin.getRewardsGroupManager().getRewardsGroupName(this.rewardsGroup));

            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public String toString() {
        return "RewardsGroupView{" +
                ", page=" + this.page +
                ", rewardsGroup=" + this.rewardsGroup +
                "}";
    }
}

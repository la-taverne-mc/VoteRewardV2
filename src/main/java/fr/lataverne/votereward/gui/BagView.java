package fr.lataverne.votereward.gui;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.managers.BagManager;
import fr.lataverne.votereward.managers.GuiManager;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.GivenReward;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BagView extends NavigableGui {

    private final Bag bag;

    public BagView(Bag bag, int page) {
        super(54, page);

        this.bag = bag;
    }

    @Override
    public @NotNull String getTitle() {
        return Helper.getStringInConfig("gui.bagSee.title");
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getRawSlot() == 49) {
            GuiManager guiManager = VoteReward.getInstance().getGuiManager();
            UUID uuid = guiManager.getOwnerOfGui(this);
            BagManager.giveBag(this.bag, Bukkit.getPlayer(uuid), Constant.MAX_NB_REWARDS_RETRIEVING);
            return EInventoryAction.Close;
        }

        return super.onInventoryClickEvent(event);
    }

    @Override
    public String toString() {
        return "BagView{" + "bag=" + this.bag + ", content=" + Arrays.toString(this.content) + ", page=" + this.page +
               "}";
    }

    @Override
    protected void setContent() {
        List<GivenReward> rewards = this.getRewardsToBeDisplayed();

        int size = rewards.size();

        for (int i = 0; i < 45; i++) {
            this.content[i] = i < size
                              ? BagView.getRewardView(rewards.get(i))
                              : new ItemStack(Material.AIR);
        }

        super.setContent();

        ItemStack getRewardsItem = new ItemStack(Material.TRIPWIRE_HOOK, 1);

        ItemMeta getRewardsMeta = getRewardsItem.getItemMeta();
        if (getRewardsMeta != null) {
            getRewardsMeta.setDisplayName(Helper.getStringInConfig("gui.bagSee.getRewards"));
            getRewardsItem.setItemMeta(getRewardsMeta);
        }

        this.content[49] = getRewardsItem;
    }

    private static @NotNull ItemStack getRewardView(@NotNull GivenReward reward) {
        ItemStack item = new ItemStack(reward.reward().getItem());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            lore.add("");
            lore.add(ChatColor.BLUE + "Expire le " +
                     reward.expirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull List<GivenReward> getRewardsToBeDisplayed() {
        if (this.bag == null) {
            return new ArrayList<>();
        }

        List<GivenReward> bagContent = this.bag.getBagContent().stream().toList();

        Pair<Integer, Integer> indexes = this.getFirstAndLastIndexes(bagContent);

        return indexes.getLeft().intValue() == -1 || indexes.getRight().intValue() == -1
               ? new ArrayList<>()
               : bagContent.subList(indexes.getLeft().intValue(), indexes.getRight().intValue());
    }
}

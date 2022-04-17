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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class BagView extends NavigableGui {

    private static final String ID_LINE = "ID: ";

    private static final Pattern ID_REWARD_LORE_PATTERN = Pattern.compile("^(ID: \\d+)$");

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

        if (event.getRawSlot() >= 0 && event.getRawSlot() < 45) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                int id = getIdOfItemClicked(clickedItem);
                if (id != -1) {
                    Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());

                    BagManager.giveReward(this.bag, player, id);

                    event.getWhoClicked().openInventory(this.getInventory());
                    this.plugin.getGuiManager().addGui(event.getWhoClicked().getUniqueId(), this);
                }
            }
        }

        return super.onInventoryClickEvent(event);
    }

    @Override
    protected void setContent() {
        List<Map.Entry<Integer, GivenReward>> rewards = this.getRewardsToBeDisplayed();

        int size = rewards.size();

        for (int i = 0; i < 45; i++) {
            if (i < size) {
                Map.Entry<Integer, GivenReward> entry = rewards.get(i);
                this.content[i] = getRewardView(entry.getKey().intValue(), entry.getValue());
            } else {
                this.content[i] = new ItemStack(Material.AIR);
            }
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

    private static @NotNull ItemStack getRewardView(int id, @NotNull GivenReward reward) {
        ItemStack item = new ItemStack(reward.reward().getItem());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            lore.add(ChatColor.GRAY + ID_LINE + id);
            lore.add("");
            lore.add(ChatColor.BLUE + "Expire le " +
                     reward.expirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull List<Map.Entry<Integer, GivenReward>> getRewardsToBeDisplayed() {
        if (this.bag == null) {
            return new ArrayList<>();
        }

        List<Map.Entry<Integer, GivenReward>> rewardsWithId = this.bag.getBagContent().stream().toList();

        Pair<Integer, Integer> indexes = this.getFirstAndLastIndexes(rewardsWithId);

        return indexes.getLeft().intValue() == -1 || indexes.getRight().intValue() == -1
               ? new ArrayList<>()
               : rewardsWithId.subList(indexes.getLeft().intValue(), indexes.getRight().intValue());
    }
}

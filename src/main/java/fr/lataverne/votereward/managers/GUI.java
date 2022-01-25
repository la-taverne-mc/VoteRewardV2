package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUI {

    private static final HashMap<UUID, GUI> guiList = new HashMap<>();

    private final UUID targetUUID;

    private ETypeGui type;

    private @Nullable Inventory inventory = null;

    private GUI(ETypeGui type, UUID targetUUID) {
        this.type = type;
        this.targetUUID = targetUUID;
    }

    public static Inventory getGUI(Player owner, ETypeGui type, int page) {
        return GUI.getGUI(owner, type, page, owner.getUniqueId());
    }

    public static Inventory getGUI(@NotNull Player owner, ETypeGui type, int page, UUID targetUUID) {
        GUI gui;

        if (GUI.guiList.containsKey(owner.getUniqueId())) {
            gui = GUI.guiList.get(owner.getUniqueId());
            if (gui.type != type) {
                gui.inventory = null;
                gui.type = type;
            }
        } else {
            gui = new GUI(type, targetUUID);
        }

        switch (type) {
            case Bag -> gui.createBagView(page);
            case Stat -> gui.createStatView(page);
        }

        return gui.inventory;
    }

    public static String getRvBagTitle() {
        return VoteReward.getInstance().getConfig().getString("gui.bagSee.title");
    }

    public static String getRvStatTitle() {
        return VoteReward.getInstance().getConfig().getString("gui.stat.title");
    }

    public static void removeGUI(UUID uuid) {
        GUI.guiList.remove(uuid);
    }

    @Override
    public @NonNls String toString() {
        return "GUI{" + "targetUUID=" + this.targetUUID + ", type=" + this.type + ", inventory=" + this.inventory + '}';
    }

    private static void setNavigationButtons(ItemStack[] content, int currentPage) {
        int previousPage = currentPage > 0 ? currentPage - 1 : -1;
        int nextPage = currentPage + 1;

        ItemStack previousPageItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta previousPageMeta = previousPageItem.getItemMeta();
        if (previousPageMeta != null) {
            previousPageMeta.setDisplayName(Helper.colorizeString(VoteReward.getInstance().getConfig().getString("gui.navigationButton.previousPage")));
            @NonNls List<String> lore = previousPageMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Page " + previousPage);
            previousPageMeta.setLore(lore);
            previousPageItem.setItemMeta(previousPageMeta);
        }

        ItemStack nextPageItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta nextPageMeta = nextPageItem.getItemMeta();
        if (nextPageMeta != null) {
            nextPageMeta.setDisplayName(Helper.colorizeString(VoteReward.getInstance().getConfig().getString("gui.navigationButton.nextPage")));
            @NonNls List<String> lore = nextPageMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Page " + nextPage);
            nextPageMeta.setLore(lore);
            nextPageItem.setItemMeta(nextPageMeta);
        }

        ItemStack emptySpaceItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta emptySpaceMeta = emptySpaceItem.getItemMeta();
        if (emptySpaceMeta != null) {
            emptySpaceMeta.setDisplayName(ChatColor.RESET + "");
            emptySpaceItem.setItemMeta(emptySpaceMeta);
        }

        content[content.length - 9] = previousPage > -1 ? previousPageItem : emptySpaceItem;
        content[content.length - 8] = emptySpaceItem;
        content[content.length - 7] = emptySpaceItem;
        content[content.length - 6] = emptySpaceItem;
        content[content.length - 5] = emptySpaceItem;
        content[content.length - 4] = emptySpaceItem;
        content[content.length - 3] = emptySpaceItem;
        content[content.length - 2] = emptySpaceItem;
        content[content.length - 1] = nextPageItem;
    }

    private void createBagView(int page) {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(null, 54, GUI.getRvBagTitle());
        }

        ItemStack[] content = new ItemStack[54];

        List<Reward> bagContent = VoteReward.getInstance().getBagManager().getOrCreateBag(this.targetUUID).getBagContent().stream().toList();

        for (int i = 0; i < 45; i++) {
            int index = i + (page * 45);
            if (index < bagContent.size()) {
                ItemStack reward = new ItemStack(bagContent.get(index).itemStack());
                ItemMeta rewardMeta = reward.getItemMeta();
                if (rewardMeta != null) {
                    @NonNls List<String> rewardLore = rewardMeta.getLore();
                    if (rewardLore == null) {
                        rewardLore = new ArrayList<>();
                    }
                    rewardLore.add(ChatColor.BLUE + "Expire le " + bagContent.get(index).expirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    rewardMeta.setLore(rewardLore);
                    reward.setItemMeta(rewardMeta);
                }

                content[i] = reward;
            } else {
                content[i] = new ItemStack(Material.AIR);
            }
        }

        GUI.setNavigationButtons(content, page);

        ItemStack getRewardsItem = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemMeta getRewardsMeta = getRewardsItem.getItemMeta();
        if (getRewardsMeta != null) {
            getRewardsMeta.setDisplayName(VoteReward.getInstance().getConfig().getString("gui.bagSee.getRewards"));
            getRewardsItem.setItemMeta(getRewardsMeta);
        }
        content[49] = getRewardsItem;

        this.inventory.setContents(content);
    }

    private void createStatView(int page) {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(null, 54, GUI.getRvStatTitle());
        }

        ItemStack[] content = new ItemStack[54];

        AchievableReward[] achievableRewards = AchievableReward.getAchievableRewards();

        DecimalFormat df = new DecimalFormat("###.##");

        for (int i = 0; i < 45; i++) {
            int index = i + (page * 45);
            if (index < achievableRewards.length) {
                ItemStack itemStack = new ItemStack(achievableRewards[index].itemStack());
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    @NonNls List<String> lore = itemMeta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }
                    lore.add(ChatColor.GRAY + "Pourcentage d'obtention :" + achievableRewards[index].percentage());
                    lore.add(ChatColor.GRAY + "Chance réelle d'obtention :" + df.format(achievableRewards[index].getRealChanceOfDrop()));
                    lore.add("");
                    lore.add(ChatColor.BLUE + "Clic pour modifier le pourcentage d'obtention");

                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                }
                content[i] = itemStack;
            } else {
                content[i] = new ItemStack(Material.AIR);
            }
        }

        GUI.setNavigationButtons(content, page);

        this.inventory.setContents(content);
    }
}
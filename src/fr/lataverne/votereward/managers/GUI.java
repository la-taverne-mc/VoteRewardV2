package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUI {

    public enum ETypeGui {
        Bag,
        Stat
    }

    private static final HashMap<UUID, GUI> guiList = new HashMap<>();

    private final Player owner;

    private ETypeGui type;
    private Inventory inventory = null;

    private GUI(Player owner, ETypeGui type) {
        this.type = type;
        this.owner = owner;
    }

    public static Inventory getGUI(Player owner, ETypeGui type, int page) {
        GUI gui;

        if (guiList.containsKey(owner.getUniqueId())) {
            gui = guiList.get(owner.getUniqueId());
            if (gui.type != type) {
                gui.inventory = null;
                gui.type = type;
            }
        } else {
            gui = new GUI(owner, type);
        }

        switch (type) {
            case Bag:
                gui.createBagView(page);
                break;
            case Stat:
                gui.createStatView(page);
                break;
            default:
                break;
        }

        return gui.inventory;
    }

    private void createBagView(int page) {
        if (this.inventory ==  null) {
            this.inventory = Bukkit.createInventory(null, 54, getRvBagTitle());
        }

        ItemStack[] content = new ItemStack[54];

        List<Reward> bagContent = Bag.getPlayerBag(owner.getUniqueId()).getBagContent();

        for (int i = 0; i < 45; i++) {
            int index = i + (page * 45);
            if (index < bagContent.size()) {
                ItemStack reward = bagContent.get(index).getItemStack().clone();
                ItemMeta rewardMeta = reward.getItemMeta();
                if (rewardMeta != null) {
                    List<String> rewardLore = rewardMeta.getLore();
                    if (rewardLore == null) {
                        rewardLore = new ArrayList<>();
                    }
                    rewardLore.add(ChatColor.BLUE + "Expire le " + bagContent.get(index).getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    rewardMeta.setLore(rewardLore);
                    reward.setItemMeta(rewardMeta);
                }

                content[i] = reward;
            }
            else {
                content[i] = null;
            }
        }

        setNavigationButtons(content, page);

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
            this.inventory = Bukkit.createInventory(null, 54, getRvStatTitle());
        }

        ItemStack[] content = new ItemStack[54];

        AchievableReward[] achievableRewards = AchievableReward.getAchievableRewards();

        DecimalFormat df = new DecimalFormat("###.##");

        for (int i = 0; i < 45; i++) {
            int index = i + (page * 45);
            if (index < achievableRewards.length) {
                ItemStack itemStack = achievableRewards[index].getItemStack().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    List<String> lore = itemMeta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }
                    lore.add(ChatColor.GRAY + "Pourcentage d'obtention :" + achievableRewards[index].getPercentage());
                    lore.add(ChatColor.GRAY + "Chance réelle d'obtention :" + df.format(achievableRewards[index].getRealChanceOfDrop()));
                    lore.add("");
                    lore.add(ChatColor.BLUE + "Clic pour modifier le pourcentage d'obtention");

                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                }
                content[i] = itemStack;
            } else {
                content[i] = null;
            }
        }

        setNavigationButtons(content, page);

        this.inventory.setContents(content);
    }

    public static void removeGUI(UUID uuid) {
        guiList.remove(uuid);
    }

    public static String getRvBagTitle() {
        return VoteReward.getInstance().getConfig().getString("gui.bagSee.title");
    }

    public static String getRvStatTitle() {
        return VoteReward.getInstance().getConfig().getString("gui.stat.title");
    }

    private static void setNavigationButtons(ItemStack[] content, int currentPage) {
        int previousPage = currentPage > 0 ? currentPage - 1 : -1;
        int nextPage = currentPage + 1;

        ItemStack previousPageItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta previousPageMeta = previousPageItem.getItemMeta();
        if (previousPageMeta != null) {
            previousPageMeta.setDisplayName(Helper.colorizeString(VoteReward.getInstance().getConfig().getString("gui.navigationButton.previousPage")));
            List<String> lore = previousPageMeta.getLore();
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
            List<String> lore = nextPageMeta.getLore();
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
}
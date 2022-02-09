package fr.lataverne.votereward.gui.admin;

import fr.lataverne.votereward.gui.EInventoryAction;
import fr.lataverne.votereward.gui.NavigableGui;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardsGroupListAdminView extends NavigableGui {

    private final Map<String, RewardsGroup> rewardsGroups;

    public RewardsGroupListAdminView(int page) {
        super(54, page);

        this.rewardsGroups = this.plugin.getRewardsGroupManager().getRewardsGroups();
    }

    @Override
    protected void setContent() {
        List<Map.Entry<String, RewardsGroup>> filteredRewardsGroups = this.getRewardsGroupsToBeDisplayed();

        int size = filteredRewardsGroups.size();

        String enabledRewardsGroup = this.plugin.getRewardsGroupManager().getEnabledRewardsGroupName();

        for (int i = 0; i < 45; i++) {
            if (i < size) {
                String name = filteredRewardsGroups.get(i).getKey();
                this.content[i] = this.getRewardsGroupView(name, name.equals(enabledRewardsGroup));
            } else {
                this.content[i] = new ItemStack(Material.AIR);
            }
        }

        super.setContent();
    }

    @Override
    public @NotNull String getTitle() {
        return this.plugin.getConfig().getString("gui.admin.rewards-group-list.title");
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();

        if (event.getRawSlot() >= 0 && event.getRawSlot() < 45) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getClick() == ClickType.SHIFT_LEFT) {
                ItemStack item = event.getCurrentItem();
                ItemMeta meta = item.getItemMeta();

                if (player.hasPermission("votereward.admin.rewardsgroups.activate") || player.isOp()) {
                    if (meta != null) {
                        String rewardsGroupName = ChatColor.stripColor(meta.getDisplayName());
                        this.plugin.getRewardsGroupManager().setEnabledRewardsGroupName(rewardsGroupName);

                        return EInventoryAction.Reopen;
                    }
                } else {
                    player.sendMessage(this.plugin.getConfig().getString("messages.error.no-permission").replace("[permission]", "votereward.admin.rewardsgroups.activate"));
                }
            }

            return EInventoryAction.Nothing;
        } else {
            return super.onInventoryClickEvent(event);
        }
    }

    private @NotNull ItemStack getRewardsGroupView(String name, boolean enabled) {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore == null) {
                lore = new ArrayList<>();
            }

            if (enabled) {
                meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + name);

                lore.add("");
                lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-list.enabled"));

                meta.setLore(lore);

                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.setDisplayName(ChatColor.GREEN + name);

                lore.add("");
                lore.add(this.plugin.getConfig().getString("gui.admin.rewards-group-list.help-for-activate"));

                meta.setLore(lore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull List<Map.Entry<String, RewardsGroup>> getRewardsGroupsToBeDisplayed() {
        if (this.rewardsGroups == null) {
            return new ArrayList<>();
        }

        List<Map.Entry<String, RewardsGroup>> rewardsGroupsList = this.rewardsGroups.entrySet().stream().toList();

        Pair<Integer, Integer> indexes = this.getFirstAndLastIndexes(rewardsGroupsList);

        return indexes.getLeft().intValue() == -1 || indexes.getRight().intValue() == -1
                ? new ArrayList<>()
                : rewardsGroupsList.subList(indexes.getLeft().intValue(), indexes.getRight().intValue());
    }

    @Override
    public String toString() {
        return "RewardsGroupListView{" +
                "page=" + this.page +
                ", rewardsGroups=" + this.rewardsGroups +
                "}";
    }
}

package fr.lataverne.votereward.gui;

import fr.lataverne.votereward.Helper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class NavigableGui extends Gui {
    @SuppressWarnings ("FieldNotUsedInToString")
    private final int slotPreviousPage;
    @SuppressWarnings ("FieldNotUsedInToString")
    private final int slotNextPage;
    protected int page;

    protected NavigableGui(int size, int page) {
        super(size);

        if (page < 0) {
            throw new IllegalArgumentException("The page must be greater than 0");
        }

        this.page = page;

        this.slotPreviousPage = this.content.length - 9;
        this.slotNextPage = this.content.length - 1;
    }

    private static @NotNull ItemStack getPreviousPageButton(int previousPage) {
        ItemStack button = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta meta = button.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(Helper.getStringInConfig("gui.navigationButton.previousPage"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Page " + previousPage);
            meta.setLore(lore);

            button.setItemMeta(meta);
        }

        return button;
    }

    private static @NotNull ItemStack getNextPageButton(int nextPage) {
        ItemStack button = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta meta = button.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(Helper.getStringInConfig("gui.navigationButton.nextPage"));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Page " + nextPage);
            meta.setLore(lore);

            button.setItemMeta(meta);
        }

        return button;
    }

    private static @NotNull ItemStack getEmptySpace() {
        ItemStack emptySpaceItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);

        ItemMeta emptySpaceMeta = emptySpaceItem.getItemMeta();
        if (emptySpaceMeta != null) {
            emptySpaceMeta.setDisplayName(ChatColor.RESET + "");
            emptySpaceItem.setItemMeta(emptySpaceMeta);
        }

        return emptySpaceItem;
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        if (rawSlot >= this.slotPreviousPage && rawSlot <= this.slotNextPage) {
            event.setCancelled(true);

            EInventoryAction action = EInventoryAction.Nothing;
            int newPage = this.page;

            if (rawSlot == this.slotNextPage) {
                newPage++;
                action = EInventoryAction.Reopen;
            } else if (rawSlot == this.slotPreviousPage) {
                newPage--;
                action = EInventoryAction.Reopen;
            }

            if (newPage >= 0) {
                this.page = newPage;
                return action;
            }
        }

        return EInventoryAction.Nothing;
    }

    @Override
    protected void setContent() {
        this.setNavigationBar();
    }

    private void setNavigationBar() {
        int previousPage = this.page > 0 ? this.page - 1 : -1;
        int nextPage = this.page + 1;

        ItemStack previousPageButton = previousPage > -1 ? NavigableGui.getPreviousPageButton(previousPage) : NavigableGui.getEmptySpace();
        ItemStack nextPageButton = NavigableGui.getNextPageButton(nextPage);

        this.content[this.content.length - 9] = previousPageButton;
        this.content[this.content.length - 8] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 7] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 6] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 5] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 4] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 3] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 2] = NavigableGui.getEmptySpace();
        this.content[this.content.length - 1] = nextPageButton;
    }

    @Contract ("null -> new")
    protected final @NotNull Pair<Integer, Integer> getFirstAndLastIndexes(Collection<?> elements) {
        if (elements == null) {
            return new ImmutablePair<>(-1, -1);
        }

        int firstIndex = this.page * 45;
        if (firstIndex > elements.size()) {
            return new ImmutablePair<>(-1, -1);
        }

        int lastIndex = (this.page + 1) * 45;
        if (lastIndex > elements.size()) {
            lastIndex = elements.size();
        }

        return new ImmutablePair<>(firstIndex, lastIndex);
    }

    @Override
    public String toString() {
        return "NavigableGUI{" +
                "content=" + Arrays.toString(this.content) +
                ", page=" + this.page +
                "}";
    }
}

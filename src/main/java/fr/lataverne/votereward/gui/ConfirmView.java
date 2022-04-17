package fr.lataverne.votereward.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ConfirmView extends Gui {

    private final Runnable action;

    private final String actionInfo;

    public ConfirmView(String actionInfo, Runnable action) {
        super(27);
        this.actionInfo = actionInfo;
        this.action = action;
    }

    @Override
    public @NotNull String getTitle() {
        return this.plugin.getConfig().getString("gui.confirm.title");
    }

    @Override
    public EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() >= 0 && event.getRawSlot() <= 26) {
            event.setCancelled(true);

            switch (event.getRawSlot()) {
                case 11 -> {
                    Bukkit.getScheduler().runTask(this.plugin, this.action);
                    this.close();
                }
                case 15 -> this.close();
            }
        }

        return EInventoryAction.Nothing;
    }

    @Override
    protected void setContent() {
        this.content[11] = this.getConfirmButton();
        this.content[13] = this.getActionInfo();
        this.content[15] = this.getCancelButton();
    }

    private @NotNull ItemStack getActionInfo() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GRAY + this.actionInfo);

            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull ItemStack getCancelButton() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.plugin.getConfig().getString("gui.confirm.cancel-button"));

            item.setItemMeta(meta);
        }

        return item;
    }

    private @NotNull ItemStack getConfirmButton() {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.plugin.getConfig().getString("gui.confirm.confirm-button"));

            item.setItemMeta(meta);
        }

        return item;
    }
}

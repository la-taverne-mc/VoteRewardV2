package fr.lataverne.votereward.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings ("ClassNamePrefixedWithPackageName")
public abstract class Gui {
    protected final ItemStack[] content;

    @SuppressWarnings ("FieldNotUsedInToString")
    private Inventory inventory = null;

    protected Gui(int size) {
        if (size <= 0 || size % 9 != 0 || size > 54) {
            throw new IllegalArgumentException("The size must be a multiple of 9, greater than 0 and less than 55");
        }

        this.content = new ItemStack[size];
    }

    public final @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(null, this.content.length, this.getTitle());
        }

        this.setContent();

        this.inventory.setContents(this.content);
        return this.inventory;
    }

    public abstract String getTitle();

    protected abstract void setContent();

    @Override
    public String toString() {
        return "GUI{" +
                "content=" + Arrays.toString(this.content) +
                "}";
    }

    public abstract EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event);
}

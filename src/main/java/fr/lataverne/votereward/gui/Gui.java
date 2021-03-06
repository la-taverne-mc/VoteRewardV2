package fr.lataverne.votereward.gui;

import fr.lataverne.votereward.VoteReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public abstract class Gui {

    protected static final String ID_LINE = "ID: ";

    protected static final Pattern ID_REWARD_LORE_PATTERN = Pattern.compile("^(ID: \\d+)$");

    protected final ItemStack[] content;

    protected final VoteReward plugin;

    private Inventory inventory = null;

    protected Gui(int size) {
        if (size <= 0 || size % 9 != 0 || size > 54) {
            throw new IllegalArgumentException("The size must be a multiple of 9, greater than 0 and less than 55");
        }

        this.plugin = VoteReward.getInstance();

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

    public abstract @NotNull String getTitle();

    public abstract EInventoryAction onInventoryClickEvent(@NotNull InventoryClickEvent event);

    protected static @NotNull ItemStack getEmptySpace() {
        ItemStack emptySpaceItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);

        ItemMeta emptySpaceMeta = emptySpaceItem.getItemMeta();
        if (emptySpaceMeta != null) {
            emptySpaceMeta.setDisplayName(ChatColor.RESET + "");
            emptySpaceItem.setItemMeta(emptySpaceMeta);
        }

        return emptySpaceItem;
    }

    protected static int getIdOfItemClicked(@NotNull ItemStack clickedItem) {
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

    protected void close() {
        UUID uuid = this.plugin.getGuiManager().getOwnerOfGui(this);

        if (uuid != null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }

            this.plugin.getGuiManager().removeGui(uuid);
        }
    }

    protected abstract void setContent();
}

package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.objects.Bag;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventListener implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public static void OnInventoryClosed(@NotNull final InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        GUI.removeGUI(player.getUniqueId());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public static void onMenuClicked(@NotNull final InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = view.getTitle();

        if (title.equals(GUI.getRvBagTitle())) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (event.getRawSlot() == 45 || event.getRawSlot() == 53) {
                int page = 0;

                ItemStack itemClicked = event.getCurrentItem();
                if (itemClicked != null && itemClicked.getItemMeta() != null) {
                    List<String> lore = itemClicked.getItemMeta().getLore();

                    if (lore != null) {
                        for (final String line : lore) {
                            try {
                                String pageStr = ChatColor.stripColor(line).replace("Page ", "");
                                page = Integer.parseInt(pageStr);
                            } catch (final NumberFormatException ignored) {
                                player.sendMessage(line);
                            }
                        }
                    }
                }

                event.getWhoClicked().openInventory(GUI.getGUI(player, ETypeGui.Bag, page));
            }

            if (event.getRawSlot() == 49) {
                player.closeInventory();
                if (Helper.playerHasPermission(player, "rv.player.bag.get")) {
                    Bag.retrievingBag(Bag.getPlayerBag(player.getUniqueId()), player);
                }
            }

            return;
        }

        if (title.equals(GUI.getRvStatTitle())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public static void onPlayerDisconnected(final @NotNull PlayerQuitEvent event) {
        Bag.getPlayerBag(event.getPlayer().getUniqueId()).saveBag();
    }
}

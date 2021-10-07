package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.objects.Bag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventListener implements Listener {

	@EventHandler (priority = EventPriority.LOWEST)
	public void OnInventoryClosed(InventoryCloseEvent event) {
		GUI.removeGUI(event.getPlayer().getUniqueId());
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onMenuClicked(InventoryClickEvent event) {
		if (event.getView().getTitle().equals(GUI.getRvBagTitle())) {
			event.setCancelled(true);

			Player player = (Player) event.getWhoClicked();

			if (event.getRawSlot() == 45 || event.getRawSlot() == 53) {
				int page = 0;

				ItemStack itemClicked = event.getCurrentItem();
				List<String> lore;
				if (itemClicked != null && itemClicked.getItemMeta() != null && (lore = itemClicked.getItemMeta().getLore()) != null) {
					for (String line : lore) {
						try {
							line = ChatColor.stripColor(line).replace("Page ", "");
							page = Integer.parseInt(line);
							break;
						} catch (NumberFormatException ignored) {
							player.sendMessage(line);
						}
					}
				}

				event.getWhoClicked().openInventory(GUI.getGUI(player, GUI.ETypeGui.Bag, page));
			}

			if (event.getRawSlot() == 49) {
				player.closeInventory();
				if (Helper.playerHasPermission(player, "rv.player.bag.get")) {
					Bag.retrievingBag(Bag.getPlayerBag(player.getUniqueId()), player);
				}
			}

			return;
		}

		if (event.getView().getTitle().equals(GUI.getRvStatTitle())) {
			event.setCancelled(true);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerDisconnected(PlayerQuitEvent event) {
		Bag.getPlayerBag(event.getPlayer().getUniqueId()).saveBag();
	}
}

package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.gui.EInventoryAction;
import fr.lataverne.votereward.gui.Gui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class EventListener implements Listener {

    @SuppressWarnings ("FieldNotUsedInToString")
    private final BagManager bagManager;

    @SuppressWarnings ("FieldNotUsedInToString")
    private final GuiManager guiManager;

    public EventListener(@NotNull VoteReward plugin) {
        this.bagManager = plugin.getBagManager();
        this.guiManager = plugin.getGuiManager();
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        Gui gui = this.guiManager.getGui(event.getWhoClicked().getUniqueId());
        if (gui != null) {
            EInventoryAction action = gui.onInventoryClickEvent(event);
            switch (action) {
                case Close -> event.getWhoClicked().closeInventory();
                case Reopen -> {
                    event.getWhoClicked().openInventory(gui.getInventory());
                    this.guiManager.addGui(event.getWhoClicked().getUniqueId(), gui);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryCloseEvent(@NotNull InventoryCloseEvent event) {
        this.guiManager.removeGui(event.getPlayer().getUniqueId());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        this.bagManager.saveBag(event.getPlayer().getUniqueId());
    }

    @Override
    public String toString() {
        return "EventListener{}";
    }
}

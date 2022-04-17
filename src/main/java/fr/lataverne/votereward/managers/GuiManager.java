package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.gui.BagView;
import fr.lataverne.votereward.gui.ConfirmView;
import fr.lataverne.votereward.gui.Gui;
import fr.lataverne.votereward.gui.admin.RewardsGroupListAdminView;
import fr.lataverne.votereward.gui.admin.RewardsGroupView;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private final Map<UUID, Gui> guis = new HashMap<>();

    public void addGui(UUID uuid, Gui gui) {
        this.guis.put(uuid, gui);
    }

    public BagView getBagView(@NotNull Player player, Bag bag) {
        Gui gui = this.getGui(player.getUniqueId());

        if (gui instanceof BagView bagView) {
            return bagView;
        } else {
            BagView bagView = new BagView(bag, 0);
            this.guis.put(player.getUniqueId(), bagView);
            return bagView;
        }
    }

    public ConfirmView getConfirmView(@NotNull Player player, String actionInfo, Runnable action) {
        ConfirmView confirmView = new ConfirmView(actionInfo, action);
        this.guis.put(player.getUniqueId(), confirmView);
        return confirmView;
    }

    public @Nullable Gui getGui(UUID uuid) {
        return this.guis.getOrDefault(uuid, null);
    }

    public @Nullable UUID getOwnerOfGui(Gui gui) {
        for (Map.Entry<UUID, Gui> entry : this.guis.entrySet()) {
            if (entry.getValue() == gui) {
                return entry.getKey();
            }
        }

        return null;
    }

    public RewardsGroupListAdminView getRewardsGroupListView(@NotNull Player player, int page) {
        Gui gui = this.getGui(player.getUniqueId());

        if (gui instanceof RewardsGroupListAdminView view) {
            return view;
        } else {
            RewardsGroupListAdminView view = new RewardsGroupListAdminView(page);
            this.guis.put(player.getUniqueId(), view);
            return view;
        }
    }

    public RewardsGroupView getRewardsGroupView(@NotNull Player player, RewardsGroup rewardsGroup, int page) {
        Gui gui = this.getGui(player.getUniqueId());

        if (gui instanceof RewardsGroupView view) {
            return view;
        } else {
            RewardsGroupView view = new RewardsGroupView(rewardsGroup, page);
            this.guis.put(player.getUniqueId(), view);
            return view;
        }
    }

    public void removeGui(UUID uuid) {
        this.guis.remove(uuid);
    }

    @Override
    public String toString() {
        return "GuiManager{" + "guis=" + this.guis + "}";
    }
}

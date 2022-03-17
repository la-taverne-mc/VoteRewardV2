package fr.lataverne.votereward.objects.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.itemreward.api.CustomItems;
import fr.lataverne.itemreward.api.objects.ICustomItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeParseException;

class ItemRewardItem extends Reward {

    private final ICustomItem customItem;

    ItemRewardItem(ICustomItem customItem) {
        this.customItem = customItem;
    }

    ItemRewardItem(ItemStack item) {
        this.customItem = CustomItems.getCustomItems(item);
    }

    @Override
    public @Nullable ItemStack getItem() {
        return this.customItem.getItemStack();
    }

    @Override
    public String toString() {
        return "ItemRewardItem{" + "customItem=" + this.customItem + "}";
    }

    @Override
    protected ERewardType getRewardType() {
        return ERewardType.ItemReward;
    }

    @Override
    protected @NotNull JsonElement toJsonReward() {
        JsonObject jsonReward = new JsonObject();

        jsonReward.addProperty("customItemType", String.valueOf(this.customItem.getCustomItemType()));
        jsonReward.addProperty("amount", this.customItem.getAmount());
        jsonReward.addProperty("level", this.customItem.getLevel());

        return jsonReward;
    }

    static boolean isCustomItem(ItemStack item) {
        return CustomItems.isCustomItem(item);
    }

    static @Nullable ItemRewardItem parseJsonReward(@NotNull JsonElement elemRoot) {
        try {
            JsonObject jsonRoot = elemRoot.getAsJsonObject();

            ICustomItem customItem = null;

            JsonObject jsonReward = jsonRoot.getAsJsonObject();

            if (jsonReward.has("customItemType")) {
                String customItemType = jsonReward.get("customItemType").getAsString();
                int amount = jsonReward.has("amount")
                             ? jsonReward.get("amount").getAsInt()
                             : 1;
                int level = jsonReward.has("level")
                            ? jsonReward.get("level").getAsInt()
                            : 1;

                customItem = CustomItems.getCustomItems(customItemType, amount, level);
            }

            return customItem != null
                   ? new ItemRewardItem(customItem)
                   : null;
        } catch (IllegalStateException | DateTimeParseException ignored) {
            return null;
        }
    }
}

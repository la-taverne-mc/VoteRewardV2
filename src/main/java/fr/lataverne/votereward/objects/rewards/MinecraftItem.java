package fr.lataverne.votereward.objects.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.votereward.utils.json.ItemStackJson;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MinecraftItem extends Reward {

    private final ItemStack item;

    MinecraftItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public String toString() {
        return "MinecraftItem{" + "itemStack=" + this.item + "}";
    }

    @Override
    protected ERewardType getRewardType() {
        return ERewardType.Minecraft;
    }

    @Override
    protected @NotNull JsonElement toJsonReward() {
        return ItemStackJson.serialize(this.item);
    }

    static @Nullable MinecraftItem parseJsonReward(@NotNull JsonElement elemReward) {
        if (elemReward.isJsonObject()) {
            JsonObject jsonReward = elemReward.getAsJsonObject();

            ItemStack item = ItemStackJson.deserialize(jsonReward);
            if (item != null) {
                return new MinecraftItem(item);
            }
        }

        return null;
    }
}

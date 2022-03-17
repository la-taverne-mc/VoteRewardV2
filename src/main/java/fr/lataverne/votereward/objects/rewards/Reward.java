package fr.lataverne.votereward.objects.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Reward {

    @Contract("_ -> new")
    public static @NotNull Reward getReward(ItemStack item) {
        return ItemRewardItem.isCustomItem(item)
               ? new ItemRewardItem(item)
               : new MinecraftItem(item);
    }

    public static @Nullable Reward parseJson(@NotNull JsonElement elemReward) {
        if (elemReward.isJsonObject()) {
            JsonObject jsonReward = elemReward.getAsJsonObject();

            if (jsonReward.has("rewardType") && jsonReward.has("reward")) {
                String rewardType = jsonReward.get("rewardType").getAsString();
                JsonElement elem = jsonReward.get("reward");

                try {
                    return switch (ERewardType.valueOf(rewardType)) {
                        case Minecraft -> MinecraftItem.parseJsonReward(elem);
                        case ItemReward -> ItemRewardItem.parseJsonReward(elem);
                    };
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
        }

        return null;
    }

    public abstract ItemStack getItem();

    public final @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("rewardType", this.getRewardType().toString());
        json.add("reward", this.toJsonReward());

        return json;
    }

    protected abstract ERewardType getRewardType();

    protected abstract @NotNull JsonElement toJsonReward();
}

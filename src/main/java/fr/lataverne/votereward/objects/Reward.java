package fr.lataverne.votereward.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.utils.json.ItemStackJson;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public record Reward(ItemStack itemStack, LocalDate expirationDate) {

    public static @Nullable Reward parseJson(@NotNull JsonElement elemReward) {
        if (elemReward.isJsonObject()) {
            JsonObject jsonReward = elemReward.getAsJsonObject();

            if (jsonReward.has("reward")) {
                ItemStack item = ItemStackJson.deserialize(jsonReward.get("reward"));
                if (item != null) {

                    LocalDate expirationDate = jsonReward.has("expirationDate")
                                               ? LocalDate.parse(jsonReward.get("expirationDate").getAsString())
                                               : LocalDate.now().plusDays(Constant.EXPIRATION_TIME);

                    return new Reward(item, expirationDate);
                }
            }
        }

        return null;
    }

    public @NotNull JsonElement toJson() {
        JsonObject jsonReward = new JsonObject();
        jsonReward.add("reward", ItemStackJson.serialize(this.itemStack));
        jsonReward.addProperty("expirationDate", this.expirationDate.toString());
        return jsonReward;
    }
}

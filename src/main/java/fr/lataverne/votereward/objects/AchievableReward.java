package fr.lataverne.votereward.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.utils.json.ItemStackJson;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AchievableReward(ItemStack reward, double percentage) {

    public static @Nullable AchievableReward parseJson(@NotNull JsonElement elemAchievableReward) {
        try {
            if (elemAchievableReward.isJsonObject()) {
                JsonObject jsonAchievableReward = elemAchievableReward.getAsJsonObject();

                if (jsonAchievableReward.has("reward") && jsonAchievableReward.has("percentage")) {
                    ItemStack reward = ItemStackJson.deserialize(jsonAchievableReward.get("reward"));
                    double percentage = jsonAchievableReward.get("percentage").getAsDouble();

                    return new AchievableReward(reward, percentage);
                }
            }
        } catch (IllegalStateException | ClassCastException ignored) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to parse the json to AchievableReward");
            VoteReward.sendMessageToConsole(ChatColor.RED + "Json : " + elemAchievableReward);
        }

        return null;
    }

    public @NotNull JsonElement toJson() {
        JsonObject jsonAchievableReward = new JsonObject();

        jsonAchievableReward.add("reward", ItemStackJson.serialize(this.reward));
        jsonAchievableReward.addProperty("percentage", this.percentage);

        return jsonAchievableReward;
    }
}

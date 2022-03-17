package fr.lataverne.votereward.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public record GivenReward(Reward reward, LocalDate expirationDate) {

    public static @Nullable GivenReward parseJson(@NotNull JsonElement elemRoot) {
        if (elemRoot.isJsonObject()) {
            JsonObject jsonRoot = elemRoot.getAsJsonObject();

            Reward reward = null;
            LocalDate expirationDate = jsonRoot.has("expirationDate")
                                       ? LocalDate.parse(jsonRoot.get("expirationDate").getAsString())
                                       : LocalDate.now().plusDays(Constant.EXPIRATION_TIME);

            if (jsonRoot.has("reward")) {
                reward = Reward.parseJson(jsonRoot.get("reward"));
            }

            if (reward != null) {
                return new GivenReward(reward, expirationDate);
            }
        }

        return null;
    }

    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();

        json.add("reward", this.reward.toJson());
        json.addProperty("expirationDate", this.expirationDate.toString());

        return json;
    }
}

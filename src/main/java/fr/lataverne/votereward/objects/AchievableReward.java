package fr.lataverne.votereward.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AchievableReward {

    private final Reward reward;

    private double percentage;

    public AchievableReward(Reward reward, double percentage) {
        this.reward = reward;
        this.percentage = percentage;
    }

    public static @Nullable AchievableReward parseJson(@NotNull JsonElement elemAchievableReward) {
        try {
            if (elemAchievableReward.isJsonObject()) {
                JsonObject jsonAchievableReward = elemAchievableReward.getAsJsonObject();

                if (jsonAchievableReward.has("reward") && jsonAchievableReward.has("percentage")) {
                    Reward reward = Reward.parseJson(jsonAchievableReward.get("reward"));
                    double percentage = jsonAchievableReward.get("percentage").getAsDouble();

                    if (reward != null) {
                        return new AchievableReward(reward, percentage);
                    }
                }
            }
        } catch (IllegalStateException | ClassCastException ignored) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to parse the json to AchievableReward");
            VoteReward.sendMessageToConsole(ChatColor.RED + "Json : " + elemAchievableReward);
        }

        return null;
    }

    public double getPercentage() {
        return this.percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Reward getReward() {
        return this.reward;
    }

    public @NotNull JsonElement toJson() {
        JsonObject jsonAchievableReward = new JsonObject();

        jsonAchievableReward.add("reward", this.reward.toJson());
        jsonAchievableReward.addProperty("percentage", this.percentage);

        return jsonAchievableReward;
    }
}

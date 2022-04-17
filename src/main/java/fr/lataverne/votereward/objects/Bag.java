package fr.lataverne.votereward.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bag {

    private final Map<Integer, GivenReward> bagContent;

    public Bag(@NotNull List<GivenReward> content) {
        this.bagContent = new HashMap<>();
        int size = content.size();
        for (int i = 0; i < size; i++) {
            this.bagContent.put(i, content.get(i));
        }

        this.verifyExpirationDates();
    }

    public void addReward(Reward reward) {
        this.bagContent.put(this.getAvailableId(), new GivenReward(reward, LocalDate.now()
                                                                                    .plusDays(Constant.EXPIRATION_TIME)));
    }

    public Set<Map.Entry<Integer, GivenReward>> getBagContent() {
        return this.bagContent.entrySet();
    }

    public @Nullable Map.Entry<Integer, GivenReward> getRandomReward() {
        return this.bagContent.isEmpty()
               ? null
               : this.bagContent.entrySet().stream().toList().get(new SecureRandom().nextInt(this.bagContent.size()));
    }

    public GivenReward getReward(int id) {
        return this.bagContent.getOrDefault(id, null);
    }

    public void removeReward(int id) {
        this.bagContent.remove(id);
    }

    public JsonElement toJson() {
        JsonArray jsonRewards = new JsonArray();

        if (!this.bagContent.isEmpty()) {
            this.bagContent.forEach((id, reward) -> jsonRewards.add(reward.toJson()));
        }

        return jsonRewards;
    }

    @Override
    public @NonNls String toString() {
        return "Bag{" + "bagContent=" + this.bagContent + "}";
    }

    public final void verifyExpirationDates() {
        this.bagContent.forEach((id, reward) -> {
            if (reward.expirationDate().isBefore(LocalDate.now())) {
                this.removeReward(id.intValue());
            }
        });
    }

    private Integer getAvailableId() {
        int id = 0;
        boolean idNotAvailable = true;

        while (idNotAvailable) {
            if (this.bagContent.containsKey(id)) {
                id++;
            } else {
                idNotAvailable = false;
            }
        }

        return id;
    }
}

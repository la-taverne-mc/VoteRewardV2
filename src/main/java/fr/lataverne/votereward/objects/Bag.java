package fr.lataverne.votereward.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Bag {
    private final Collection<Reward> bagContent;

    public Bag(Collection<Reward> content) {
        this.bagContent = new ArrayList<>(content);
        this.verifyExpirationDates();
    }

    public void addReward(Reward item) {
        this.bagContent.add(item);
    }

    public Collection<Reward> getBagContent() {
        return Collections.unmodifiableCollection(this.bagContent);
    }

    public @Nullable Reward getRandomReward() {
        if (this.bagContent.isEmpty()) {
            return null;
        } else {
            return this.bagContent.stream().toList().get(new SecureRandom().nextInt(this.bagContent.size()));
        }
    }

    public void removeReward(Reward reward) {
        this.bagContent.remove(reward);
    }

    public JsonElement toJson() {
        JsonArray jsonRewards = new JsonArray();

        this.bagContent.forEach(reward -> jsonRewards.add(reward.toJson()));

        return jsonRewards;
    }

    @Override
    public @NonNls String toString() {
        return "Bag{" + "bagContent=" + this.bagContent + "}";
    }

    public final void verifyExpirationDates() {
        if (!this.bagContent.isEmpty()) {
            this.bagContent.removeIf(reward -> reward.expirationDate().isBefore(LocalDate.now()));
        }
    }
}

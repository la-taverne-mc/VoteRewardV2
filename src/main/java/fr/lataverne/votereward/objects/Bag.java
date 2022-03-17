package fr.lataverne.votereward.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.objects.rewards.Reward;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Bag {

    private final Collection<GivenReward> bagContent;

    public Bag(Collection<GivenReward> content) {
        this.bagContent = new ArrayList<>(content);
        this.verifyExpirationDates();
    }

    public void addReward(Reward reward) {
        this.bagContent.add(new GivenReward(reward, LocalDate.now().plusDays(Constant.EXPIRATION_TIME)));
    }

    public Collection<GivenReward> getBagContent() {
        return Collections.unmodifiableCollection(this.bagContent);
    }

    public @Nullable GivenReward getRandomReward() {
        return this.bagContent.isEmpty()
               ? null
               : this.bagContent.stream().toList().get(new SecureRandom().nextInt(this.bagContent.size()));
    }

    public void removeReward(GivenReward reward) {
        this.bagContent.remove(reward);
    }

    public JsonElement toJson() {
        JsonArray jsonRewards = new JsonArray();

        if (!this.bagContent.isEmpty()) {
            this.bagContent.forEach(reward -> jsonRewards.add(reward.toJson()));
        }

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

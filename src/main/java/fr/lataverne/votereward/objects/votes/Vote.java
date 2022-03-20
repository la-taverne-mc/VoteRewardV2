package fr.lataverne.votereward.objects.votes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public record Vote(LocalDate date) {

    public static @Nullable Vote parse(@NotNull JsonElement elem) {
        if (elem.isJsonObject()) {
            JsonObject json = elem.getAsJsonObject();

            if (json.has("date")) {
                LocalDate date = LocalDate.parse(json.get("date").getAsString());
                return new Vote(date);
            }
        }

        return null;
    }

    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("date", this.date.toString());

        return json;
    }
}

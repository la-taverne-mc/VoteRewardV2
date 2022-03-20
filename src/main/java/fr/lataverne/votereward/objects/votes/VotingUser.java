package fr.lataverne.votereward.objects.votes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class VotingUser {

    private final UUID uuid;

    private final List<Vote> votes = new ArrayList<>();

    public VotingUser(UUID uuid) {
        this.uuid = uuid;
    }

    public VotingUser(UUID uuid, Collection<Vote> votes) {
        this.uuid = uuid;
        this.votes.addAll(votes);
    }

    public static @Nullable VotingUser parse(@NotNull JsonElement elem) {
        if (elem.isJsonObject()) {
            JsonObject json = elem.getAsJsonObject();

            if (json.has("uuid") && json.has("votes")) {
                UUID uuid = UUID.fromString(json.get("uuid").getAsString());
                JsonArray jsonVotes = json.get("votes").getAsJsonArray();

                List<Vote> votes = new ArrayList<>();

                jsonVotes.forEach(jsonVote -> {
                    Vote vote = Vote.parse(jsonVote);
                    if (vote != null) {
                        votes.add(vote);
                    }
                });

                return new VotingUser(uuid, votes);
            }
        }

        return null;
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public List<Vote> getVotes() {
        return Collections.unmodifiableList(this.votes);
    }

    public List<Vote> getVotes(ETimeRange arg) {
        LocalDate now = LocalDate.now();

        return switch (arg) {
            case ALL_TIME -> this.getVotes();
            case YEAR -> this.votes.stream().filter(v -> v.date().getYear() == now.getYear()).toList();
            case MONTH -> this.votes.stream()
                                    .filter(v -> v.date().getYear() == now.getYear() &&
                                                 v.date().getMonth() == now.getMonth())
                                    .toList();
            case WEEK -> this.votes.stream().filter(v -> isInWeek(now, v.date())).toList();
            case DAY -> this.votes.stream()
                                  .filter(v -> v.date().getYear() == now.getYear() &&
                                               v.date().getDayOfYear() == now.getDayOfYear())
                                  .toList();
        };
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();

        JsonArray jsonVotes = new JsonArray();

        this.votes.stream().map(Vote::toJson).forEach(jsonVotes::add);

        json.addProperty("uuid", this.uuid.toString());
        json.add("votes", jsonVotes);

        return json;
    }

    private static boolean isInWeek(@NotNull LocalDate dateOfWeek, @NotNull LocalDate dateToTest) {
        DayOfWeek day = dateOfWeek.getDayOfWeek();

        LocalDate monday = switch (day) {
            case MONDAY -> dateOfWeek;
            case TUESDAY -> dateOfWeek.plusDays(-1);
            case WEDNESDAY -> dateOfWeek.plusDays(-2);
            case THURSDAY -> dateOfWeek.plusDays(-3);
            case FRIDAY -> dateOfWeek.plusDays(-4);
            case SATURDAY -> dateOfWeek.plusDays(-5);
            case SUNDAY -> dateOfWeek.plusDays(-6);
        };

        LocalDate sunday = monday.plusDays(6);

        return dateToTest.getYear() == dateOfWeek.getYear() && dateToTest.getDayOfYear() >= monday.getDayOfYear() &&
               dateToTest.getDayOfYear() <= sunday.getDayOfYear();
    }
}

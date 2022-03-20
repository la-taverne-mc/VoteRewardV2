package fr.lataverne.votereward.objects.votes;

import org.jetbrains.annotations.NotNull;

public enum ETimeRange {
    ALL_TIME,
    YEAR,
    MONTH,
    WEEK,
    DAY;

    @Override
    public @NotNull String toString() {
        return switch (this) {
            case ALL_TIME -> "all-time";
            case YEAR -> "year";
            case MONTH -> "month";
            case WEEK -> "week";
            case DAY -> "day";
        };
    }
}

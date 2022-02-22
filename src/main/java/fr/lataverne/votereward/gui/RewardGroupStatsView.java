package fr.lataverne.votereward.gui;

import fr.lataverne.votereward.Helper;
import org.jetbrains.annotations.NotNull;

public class RewardGroupStatsView extends NavigableGui {

    public RewardGroupStatsView(int page) {
        super(54, page);
    }

    @Override
    public @NotNull String getTitle() {
        return Helper.getStringInConfig("gui.stat.title");
    }
}

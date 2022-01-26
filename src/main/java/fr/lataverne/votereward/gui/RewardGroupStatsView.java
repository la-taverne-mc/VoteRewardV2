package fr.lataverne.votereward.gui;

import fr.lataverne.votereward.Helper;

public class RewardGroupStatsView extends NavigableGui {

    public RewardGroupStatsView(int page) {
        super(54, page);
    }

    @Override
    public String getTitle() {
        return Helper.getStringInConfig("gui.stat.title");
    }
}

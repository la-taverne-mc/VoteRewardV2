package fr.lataverne.votereward.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.Reward;
import fr.lataverne.votereward.objects.RewardsGroup;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RewardsGroupManager {
    private static final String REWARD_GROUPS_FOLDER = "plugins/VoteReward/RewardGroups/";

    private final Map<String, RewardsGroup> rewardGroups = new HashMap<>();

    private @Nullable String enabledRewardGroupName = null;

    private static void writeRewardGroupOnFile(@NotNull String name, @NotNull RewardsGroup rewardsGroup, boolean enabled) {
        try (FileWriter fileWriter = new FileWriter(RewardsGroupManager.REWARD_GROUPS_FOLDER + "/" + name, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(fileWriter)
        ) {
            jsonWriter.beginObject();
            jsonWriter.name("name").value(name);
            jsonWriter.name("rewardGroup").jsonValue(rewardsGroup.toJson().toString());

            if (enabled) {
                jsonWriter.name("enabled").value(enabled);
            }

            jsonWriter.endObject();
        } catch (IOException e) {
            VoteReward.sendMessageToConsole("Unable to create the " + name + " file");
        } catch (SecurityException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "SecurityException: " + e.getMessage());
        }
    }

    private static @Nullable Triple<String, RewardsGroup, Boolean> parseRewardFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);

            Iterator<JsonElement> jsonParser = new JsonStreamParser(reader);

            JsonObject jsonFile = jsonParser.next().getAsJsonObject();

            if (jsonFile.has("name") && jsonFile.has("rewardGroup")) {
                String name = jsonFile.get("name").getAsString();
                RewardsGroup rewardsGroup = RewardsGroup.parseJson(jsonFile.get("rewardGroup"));
                boolean enabled = jsonFile.has("enabled") && jsonFile.get("enabled").getAsBoolean();

                return new ImmutableTriple<>(name, rewardsGroup, enabled);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "The \"" + file.getPath() + "\" file wasn't found");
        } catch (IOException | IllegalArgumentException | NoSuchElementException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Error : " + e.getMessage());
        }

        return null;
    }

    private static @Nullable File getRewardGroupsFolder() {
        File rewardGroupsFolder = new File(RewardsGroupManager.REWARD_GROUPS_FOLDER);

        if (!rewardGroupsFolder.exists()) {
            try {
                if (!rewardGroupsFolder.mkdir()) {
                    VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to create reward groups folder");
                    return null;
                }
            } catch (SecurityException e) {
                VoteReward.sendMessageToConsole(ChatColor.RED + "SecurityException: " + e.getMessage());
                return null;
            }
        }

        return rewardGroupsFolder;
    }

    public void loadRewardGroups() {
        File rewardGroupsFolder = RewardsGroupManager.getRewardGroupsFolder();

        if (rewardGroupsFolder == null) {
            return;
        }

        File[] files = rewardGroupsFolder.listFiles();

        if (files != null) {
            VoteReward.sendMessageToConsole(ChatColor.GOLD + "Loading reward groups");

            this.rewardGroups.clear();
            this.enabledRewardGroupName = null;

            for (File file : files) {
                Triple<String, RewardsGroup, Boolean> parsedFile = RewardsGroupManager.parseRewardFile(file);
                if (parsedFile != null) {
                    this.rewardGroups.put(parsedFile.getLeft(), parsedFile.getMiddle());

                    if (parsedFile.getRight().booleanValue()) {
                        this.enabledRewardGroupName = parsedFile.getLeft();
                    }
                }
            }

            VoteReward.sendMessageToConsole(ChatColor.GREEN + "Completed loading reward groups");
        }
    }

    public @Nullable RewardsGroup getRewardGroup(String name) {
        return this.rewardGroups.getOrDefault(name, null);
    }

    public void saveRewardGroups() {
        for (Map.Entry<String, RewardsGroup> entry : this.rewardGroups.entrySet()) {
            String name = entry.getKey();
            RewardsGroup rewardsGroup = entry.getValue();
            boolean enabled = name.equals(this.enabledRewardGroupName);

            RewardsGroupManager.writeRewardGroupOnFile(name, rewardsGroup, enabled);
        }
    }

    public @Nullable RewardsGroup createNewRewardGroup(String name) {
        if (this.rewardGroups.containsKey(name)) {
            return null;
        }

        RewardsGroup rewardsGroup = new RewardsGroup(new ArrayList<>());
        this.rewardGroups.put(name, rewardsGroup);
        return rewardsGroup;
    }

    public int getNumberOfAchievableRewards() {
        RewardsGroup enableRewardsGroup = this.rewardGroups.get(this.enabledRewardGroupName);

        return enableRewardsGroup != null ? enableRewardsGroup.getNumberOfReward() : 0;
    }

    public @Nullable Reward getRandomReward() {
        RewardsGroup enableRewardsGroup = this.rewardGroups.get(this.enabledRewardGroupName);

        return enableRewardsGroup != null ? enableRewardsGroup.getRandomReward() : null;
    }

    @Override
    public String toString() {
        return "RewardGroupManager{" +
                "rewardGroups=" + this.rewardGroups +
                ", enabledRewardGroupName='" + this.enabledRewardGroupName + "'" +
                "}";
    }
}
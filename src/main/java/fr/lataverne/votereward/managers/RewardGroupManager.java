package fr.lataverne.votereward.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.Reward;
import fr.lataverne.votereward.objects.RewardGroup;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RewardGroupManager {
    private static final String REWARD_GROUPS_FOLDER = "plugins/VoteReward/RewardGroups/";

    private final Map<String, RewardGroup> rewardGroups = new HashMap<>();

    private @Nullable String enabledRewardGroupName = null;

    private static void writeRewardGroupOnFile(@NotNull String name, @NotNull RewardGroup rewardGroup, boolean enabled) {
        try (FileWriter fileWriter = new FileWriter(RewardGroupManager.REWARD_GROUPS_FOLDER + "/" + name, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(fileWriter)
        ) {
            jsonWriter.beginObject();
            jsonWriter.name("name").value(name);
            jsonWriter.name("rewardGroup").jsonValue(rewardGroup.toJson().toString());

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

    private static @Nullable Triple<String, RewardGroup, Boolean> parseRewardFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);

            Iterator<JsonElement> jsonParser = new JsonStreamParser(reader);

            JsonObject jsonFile = jsonParser.next().getAsJsonObject();

            if (jsonFile.has("name") && jsonFile.has("rewardGroup")) {
                String name = jsonFile.get("name").getAsString();
                RewardGroup rewardGroup = RewardGroup.parseJson(jsonFile.get("rewardGroup"));
                boolean enabled = jsonFile.has("enabled") && jsonFile.get("enabled").getAsBoolean();

                return new ImmutableTriple<>(name, rewardGroup, enabled);
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
        File rewardGroupsFolder = new File(RewardGroupManager.REWARD_GROUPS_FOLDER);

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
        File rewardGroupsFolder = RewardGroupManager.getRewardGroupsFolder();

        if (rewardGroupsFolder == null) {
            return;
        }

        File[] files = rewardGroupsFolder.listFiles();

        if (files != null) {
            VoteReward.sendMessageToConsole(ChatColor.GOLD + "Loading reward groups");

            this.rewardGroups.clear();
            this.enabledRewardGroupName = null;

            for (File file : files) {
                Triple<String, RewardGroup, Boolean> parsedFile = RewardGroupManager.parseRewardFile(file);
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

    public @Nullable RewardGroup getRewardGroup(String name) {
        return this.rewardGroups.getOrDefault(name, null);
    }

    public void saveRewardGroups() {
        for (Map.Entry<String, RewardGroup> entry : this.rewardGroups.entrySet()) {
            String name = entry.getKey();
            RewardGroup rewardGroup = entry.getValue();
            boolean enabled = name.equals(this.enabledRewardGroupName);

            RewardGroupManager.writeRewardGroupOnFile(name, rewardGroup, enabled);
        }
    }

    public @Nullable RewardGroup createNewRewardGroup(String name) {
        if (this.rewardGroups.containsKey(name)) {
            return null;
        }

        RewardGroup rewardGroup = new RewardGroup(new ArrayList<>());
        this.rewardGroups.put(name, rewardGroup);
        return rewardGroup;
    }

    public int getNumberOfAchievableRewards() {
        RewardGroup enableRewardGroup = this.rewardGroups.get(this.enabledRewardGroupName);

        return enableRewardGroup != null ? enableRewardGroup.getNumberOfReward() : 0;
    }

    public @Nullable Reward getRandomReward() {
        RewardGroup enableRewardGroup = this.rewardGroups.get(this.enabledRewardGroupName);

        return enableRewardGroup != null ? enableRewardGroup.getRandomReward() : null;
    }

    @Override
    public String toString() {
        return "RewardGroupManager{" +
                "rewardGroups=" + this.rewardGroups +
                ", enabledRewardGroupName='" + this.enabledRewardGroupName + "'" +
                "}";
    }
}

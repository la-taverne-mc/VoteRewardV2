package fr.lataverne.votereward.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.RewardsGroup;
import fr.lataverne.votereward.objects.rewards.Reward;
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

    private final Map<String, RewardsGroup> rewardsGroups = new HashMap<>();

    private @Nullable String enabledRewardsGroupName = null;

    public void createNewRewardsGroup(String name) {
        RewardsGroup rewardsGroup = new RewardsGroup(new ArrayList<>());
        this.rewardsGroups.put(name, rewardsGroup);
    }

    public boolean deleteRewardsGroup(String name) {
        File file = new File(RewardsGroupManager.REWARD_GROUPS_FOLDER + name);
        if (file.exists()) {
            boolean success = file.delete();
            if (!success) {
                return false;
            }
        }

        this.rewardsGroups.remove(name);
        return true;
    }

    public @Nullable String getEnabledRewardsGroupName() {
        return this.enabledRewardsGroupName;
    }

    public void setEnabledRewardsGroupName(@Nullable String rewardsGroupName) {
        this.enabledRewardsGroupName = rewardsGroupName;
    }

    public @Nullable Reward getRandomReward() {
        RewardsGroup enableRewardsGroup = this.rewardsGroups.get(this.enabledRewardsGroupName);

        return enableRewardsGroup != null
               ? enableRewardsGroup.getRandomReward()
               : null;
    }

    public @Nullable RewardsGroup getRewardGroup(String name) {
        return this.rewardsGroups.getOrDefault(name, null);
    }

    public @Nullable String getRewardsGroupName(RewardsGroup rewardsGroup) {
        for (Map.Entry<String, RewardsGroup> entry : this.rewardsGroups.entrySet()) {
            if (entry.getValue() == rewardsGroup) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Map<String, RewardsGroup> getRewardsGroups() {
        return Collections.unmodifiableMap(this.rewardsGroups);
    }

    public void loadRewardGroups() {
        VoteReward.sendMessageToConsole(ChatColor.GOLD + "Loading rewards groups");

        File rewardGroupsFolder = RewardsGroupManager.getRewardGroupsFolder();

        if (rewardGroupsFolder == null) {
            return;
        }

        File[] files = rewardGroupsFolder.listFiles();

        if (files == null) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to retrieve the rewards groups files");
            return;
        }

        int count = 0;

        this.rewardsGroups.clear();
        this.enabledRewardsGroupName = null;

        for (File file : files) {
            try {
                Triple<String, RewardsGroup, Boolean> parsedFile = RewardsGroupManager.parseRewardFile(file);
                if (parsedFile != null) {
                    String name = parsedFile.getLeft();
                    RewardsGroup rewardsGroup = parsedFile.getMiddle();
                    boolean enabled = parsedFile.getRight().booleanValue();

                    StringBuilder message = new StringBuilder(
                            "[REWARDS GROUP] " + name + ": " + ChatColor.GREEN + "loaded");

                    this.rewardsGroups.put(name, rewardsGroup);

                    if (enabled) {
                        this.enabledRewardsGroupName = parsedFile.getLeft();
                        message.append(ChatColor.RESET).append(" and ").append(ChatColor.GREEN).append("Enabled");
                    }

                    VoteReward.sendMessageToConsole(message.toString());

                    count++;
                }
            } catch (Exception e) {
                VoteReward.sendMessageToConsole(
                        "[REWARDS GROUP] " + file.getName() + ": " + ChatColor.RED + "not loaded");
                VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
            }
        }

        ChatColor color = count == files.length
                          ? ChatColor.GREEN
                          : ChatColor.GOLD;

        VoteReward.sendMessageToConsole(
                color + Integer.toString(count) + " out of " + files.length + " rewards groups loaded");
    }

    public void saveRewardGroups() {
        VoteReward.sendMessageToConsole(ChatColor.GOLD + "Saving rewards groups");

        int count = 0;

        for (Map.Entry<String, RewardsGroup> entry : this.rewardsGroups.entrySet()) {
            if (this.saveRewardsGroup(entry.getKey())) {
                count++;
            }
        }

        ChatColor color = count == this.rewardsGroups.size()
                          ? ChatColor.GREEN
                          : ChatColor.GOLD;

        VoteReward.sendMessageToConsole(
                color + Integer.toString(count) + " out of " + this.rewardsGroups.size() + " rewards groups saved");
    }

    public boolean saveRewardsGroup(String name) {
        RewardsGroup rewardsGroup = this.rewardsGroups.get(name);

        if (rewardsGroup != null) {
            boolean enabled = name.equals(this.enabledRewardsGroupName);

            try {
                RewardsGroupManager.writeRewardGroupOnFile(name, rewardsGroup, enabled);
                VoteReward.sendMessageToConsole("[REWARDS GROUP] " + name + ": " + ChatColor.GREEN + "saved");
                return true;
            } catch (Exception e) {
                VoteReward.sendMessageToConsole("[REWARDS GROUP] " + name + ": " + ChatColor.RED + "not saved");
                VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "RewardGroupManager{" + "rewardGroups=" + this.rewardsGroups + ", enabledRewardGroupName='" +
               this.enabledRewardsGroupName + "'" + "}";
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

    private static @Nullable Triple<String, RewardsGroup, Boolean> parseRewardFile(File file) throws Exception {
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
            throw new Exception("The \"" + file.getName() + "\" file wasn't found");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new Exception("The json is invalid in the \"" + file.getName() + "\" file");
        } catch (IOException e) {
            throw new Exception("Unable to open the \"" + file.getName() + "\" file");
        }

        return null;
    }

    private static void writeRewardGroupOnFile(@NotNull String name, @NotNull RewardsGroup rewardsGroup, boolean enabled) throws Exception {
        try (FileWriter fileWriter = new FileWriter(RewardsGroupManager.REWARD_GROUPS_FOLDER + "/" +
                                                    name, StandardCharsets.UTF_8); JsonWriter jsonWriter = new JsonWriter(fileWriter)) {
            jsonWriter.beginObject();
            jsonWriter.name("name").value(name);
            jsonWriter.name("rewardGroup").jsonValue(rewardsGroup.toJson().toString());

            if (enabled) {
                jsonWriter.name("enabled").value(enabled);
            }

            jsonWriter.endObject();
        } catch (IOException | SecurityException ignored) {
            throw new Exception("Unable to create the " + name + " file");
        }
    }
}

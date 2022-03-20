package fr.lataverne.votereward.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.votes.ETopVoteArg;
import fr.lataverne.votereward.objects.votes.VotingUser;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VotingUserManager {

    private static final String VOTING_USER_FOLDER = "plugins/VoteReward/VotingUsers/";

    private final Map<UUID, VotingUser> votingUsers = new HashMap<>();

    public @Nullable VotingUser getVotingUser(UUID uuid) {
        if (this.votingUsers.containsKey(uuid)) {
            return this.votingUsers.get(uuid);
        } else {
            VotingUser votingUser = new VotingUser(uuid);
            this.votingUsers.put(uuid, votingUser);
            return votingUser;
        }
    }

    public Collection<VotingUser> getVotingUsers(ETopVoteArg topVoteArg) {
        return this.votingUsers.values().stream().filter(v -> !v.getVotes(topVoteArg).isEmpty()).toList();
    }

    public void loadVotingUsers() {
        File folder = getVotingUserFolder();

        if (folder == null) {
            return;
        }

        File[] files = folder.listFiles();

        if (files != null) {
            VoteReward.sendMessageToConsole(ChatColor.GOLD + "Voting users loading");

            int count = 0;

            this.votingUsers.clear();

            for (File file : files) {
                try {
                    VotingUser votingUser = parseVotingUser(file);

                    if (votingUser != null) {
                        VoteReward.sendMessageToConsole(
                                "[VOTING USER] " + votingUser.getUUID() + ": " + ChatColor.GREEN + "loaded");
                        this.votingUsers.put(votingUser.getUUID(), votingUser);
                        count++;
                    }
                } catch (Exception e) {
                    VoteReward.sendMessageToConsole(
                            "[VOTING USER] " + file.getName() + ": " + ChatColor.RED + "not loaded");
                    VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
                }
            }

            ChatColor color = count == files.length
                              ? ChatColor.GREEN
                              : ChatColor.GOLD;

            VoteReward.sendMessageToConsole(
                    color + Integer.toString(count) + " out of " + files.length + " voting users loaded");
        }
    }

    public void saveVotingUsers() {
        VoteReward.sendMessageToConsole(ChatColor.GOLD + "Saving voting users");

        int count = 0;

        for (VotingUser votingUser : this.votingUsers.values()) {
            try {
                writeVotingUserInFile(votingUser);
                VoteReward.sendMessageToConsole(
                        "[VOTING USER] " + votingUser.getUUID() + ": " + ChatColor.GREEN + "saved");
                count++;
            } catch (Exception e) {
                VoteReward.sendMessageToConsole(
                        "[VOTING USER] " + votingUser.getUUID() + ": " + ChatColor.RED + "not saved");
                VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
            }
        }

        ChatColor color = count == this.votingUsers.size()
                          ? ChatColor.GREEN
                          : ChatColor.GOLD;

        VoteReward.sendMessageToConsole(
                color + Integer.toString(count) + " out of " + this.votingUsers.size() + " voting users saved");
    }

    private static @Nullable File getVotingUserFolder() {
        File folder = new File(VOTING_USER_FOLDER);

        if (!folder.exists()) {
            try {
                if (!folder.mkdir()) {
                    VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to create voting user folder");
                    return null;
                }
            } catch (SecurityException e) {
                VoteReward.sendMessageToConsole(ChatColor.RED + "SecurityException: " + e.getMessage());
                return null;
            }
        }

        return folder;
    }

    private static @Nullable VotingUser parseVotingUser(File file) throws Exception {
        if (file == null) {
            return null;
        }

        try {
            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);

            Iterator<JsonElement> jsonParser = new JsonStreamParser(reader);

            JsonElement jsonFile = jsonParser.next();

            reader.close();

            return VotingUser.parse(jsonFile);
        } catch (IllegalStateException | NoSuchElementException e) {
            throw new Exception("The json is invalid in the \"" + file.getName() + "\" file");
        } catch (IOException e) {
            throw new Exception("Unable to open the \"" + file.getName() + "\" file");
        }
    }

    private static void writeVotingUserInFile(VotingUser votingUser) throws Exception {
        try {
            Writer writer = new FileWriter(VOTING_USER_FOLDER + votingUser.getUUID(), StandardCharsets.UTF_8);

            new Gson().toJson(votingUser.toJson(), writer);

            writer.close();
        } catch (IOException ignored) {
            throw new Exception("Unable to open the \"" + votingUser.getUUID() + "\" file");
        } catch (JsonIOException ignored) {
            throw new Exception("Unable to write in the \"" + votingUser.getUUID() + "\" file");
        }
    }
}

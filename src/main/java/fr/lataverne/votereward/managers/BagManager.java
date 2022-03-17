package fr.lataverne.votereward.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonWriter;
import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.GivenReward;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BagManager {

    private static final String BAG_FOLDER = "plugins/VoteReward/Bags/";

    private static final String NB_REWARDS = "[nb-rewards]";

    private final HashMap<UUID, Bag> bags = new HashMap<>();

    public static void giveBag(@NotNull Bag bag, Player player, int maxNbRewardsRetrieving) {
        int nbRewardsRetrieving = 0;
        int nbItemInBag = bag.getBagContent().size();

        boolean giveReward = BagManager.inventoryPlayerHasEmptySlot(player) && nbRewardsRetrieving < nbItemInBag &&
                             nbRewardsRetrieving < maxNbRewardsRetrieving;
        while (giveReward) {
            GivenReward reward = bag.getRandomReward();
            player.getInventory().addItem(new ItemStack(reward.reward().getItem()));
            bag.removeReward(reward);
            nbRewardsRetrieving++;

            giveReward = BagManager.inventoryPlayerHasEmptySlot(player) && nbRewardsRetrieving < nbItemInBag &&
                         nbRewardsRetrieving < maxNbRewardsRetrieving;
        }

        String message;
        if (nbRewardsRetrieving == 1) {
            message = VoteReward.getInstance().getConfig().getString("messages.reward.get.one");
        } else if (nbRewardsRetrieving > 1) {
            message = VoteReward.getInstance()
                                .getConfig()
                                .getString("messages.reward.get.multiple")
                                .replace(BagManager.NB_REWARDS, Integer.toString(nbRewardsRetrieving));
        } else {
            message = VoteReward.getInstance().getConfig().getString("messages.reward.get.any");
        }

        Helper.sendMessageToPlayer(player, message);
    }

    public Bag getOrCreateBag(UUID uuid) {
        Bag bag;

        if (this.bags.containsKey(uuid)) {
            bag = this.bags.get(uuid);
            bag.verifyExpirationDates();
        } else {
            bag = new Bag(new ArrayList<>());
            this.bags.put(uuid, bag);
        }

        return bag;
    }

    public List<String> getOwnerNames() {
        return this.bags.keySet()
                        .stream()
                        .map(Bukkit::getOfflinePlayer)
                        .filter(offlinePlayer -> offlinePlayer != null && offlinePlayer.hasPlayedBefore())
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.toList());
    }

    public void loadBags() {
        File bagsFolder = BagManager.getBagsFolder();

        if (bagsFolder == null) {
            return;
        }

        File[] files = bagsFolder.listFiles();

        if (files != null) {
            VoteReward.sendMessageToConsole(ChatColor.GOLD + "Bags loading");

            int count = 0;

            this.bags.clear();

            for (File file : files) {
                try {
                    Pair<UUID, Bag> ownerAndBag = BagManager.parseBagFile(file);
                    if (ownerAndBag != null) {
                        this.bags.put(ownerAndBag.getKey(), ownerAndBag.getValue());
                        VoteReward.sendMessageToConsole(
                                "[BAG] " + ownerAndBag.getKey() + ": " + ChatColor.GREEN + "loaded");
                        count++;
                    }
                } catch (Exception e) {
                    VoteReward.sendMessageToConsole("[BAG] " + file.getName() + ": " + ChatColor.RED + "not loaded");
                    VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
                }
            }

            ChatColor color = count == files.length
                              ? ChatColor.GREEN
                              : ChatColor.GOLD;

            VoteReward.sendMessageToConsole(
                    color + Integer.toString(count) + " out of " + files.length + " bags loaded");
        }
    }

    public boolean saveBag(UUID uuid) {
        Bag bag = this.bags.get(uuid);

        if (bag != null) {
            try {
                BagManager.writeBagOnFile(uuid, bag);
                VoteReward.sendMessageToConsole("[BAG] " + uuid + ": " + ChatColor.GREEN + "saved");
                return true;
            } catch (Exception e) {
                VoteReward.sendMessageToConsole("[BAG] " + uuid + ": " + ChatColor.RED + "not saved");
                VoteReward.sendMessageToConsole(ChatColor.RED + "error: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    public void saveBags() {
        VoteReward.sendMessageToConsole(ChatColor.GOLD + "Saving bags");

        int count = 0;

        for (Map.Entry<UUID, Bag> entry : this.bags.entrySet()) {
            if (this.saveBag(entry.getKey())) {
                count++;
            }
        }

        ChatColor color = count == this.bags.size()
                          ? ChatColor.GREEN
                          : ChatColor.GOLD;

        VoteReward.sendMessageToConsole(
                color + Integer.toString(count) + " out of " + this.bags.size() + " bags saved");
    }

    @Override
    public String toString() {
        return "BagManager{" + "bags=" + this.bags + "}";
    }

    private static @Nullable File getBagsFolder() {
        File bagsFolder = new File(BagManager.BAG_FOLDER);

        if (!bagsFolder.exists()) {
            try {
                if (!bagsFolder.mkdir()) {
                    VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to create bags");
                    return null;
                }
            } catch (SecurityException e) {
                VoteReward.sendMessageToConsole(ChatColor.RED + "SecurityException: " + e.getMessage());
                return null;
            }
        }

        return bagsFolder;
    }

    private static boolean inventoryPlayerHasEmptySlot(@NotNull Player player) {
        ItemStack[] inventoryContent = player.getInventory().getContents();

        return IntStream.range(0, Constant.PLAYER_INVENTORY_SIZE).anyMatch(i -> inventoryContent[i] == null);
    }

    private static @Nullable Pair<UUID, Bag> parseBagFile(File file) throws Exception {
        if (file == null) {
            return null;
        }

        FileReader reader;

        try {
            reader = new FileReader(file, StandardCharsets.UTF_8);

            Iterator<JsonElement> jsonParser = new JsonStreamParser(reader);

            JsonObject jsonFile = jsonParser.next().getAsJsonObject();

            if (jsonFile.has("owner") && jsonFile.has("bag")) {
                UUID owner = UUID.fromString(jsonFile.get("owner").getAsString());
                JsonArray jsonBag = jsonFile.getAsJsonArray("bag");

                Collection<GivenReward> content = new ArrayList<>();
                jsonBag.forEach(jsonReward -> {
                    GivenReward reward = GivenReward.parseJson(jsonReward);
                    if (reward != null) {
                        content.add(reward);
                    }
                });

                return new ImmutablePair<>(owner, new Bag(content));
            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new Exception("The \"" + file.getName() + "\" file wasn't found");
        } catch (IllegalStateException | NoSuchElementException e) {
            throw new Exception("The json is invalid in the \"" + file.getName() + "\" file");
        } catch (IOException e) {
            throw new Exception("Unable to open the \"" + file.getName() + "\" file");
        }

        return null;
    }

    private static void writeBagOnFile(@NotNull UUID uuid, @NotNull Bag bag) throws Exception {
        try (FileWriter fileWriter = new FileWriter(BagManager.BAG_FOLDER + "/" +
                                                    uuid, StandardCharsets.UTF_8); JsonWriter jsonWriter = new JsonWriter(fileWriter)) {
            jsonWriter.beginObject();
            jsonWriter.name("owner").value(uuid.toString());
            jsonWriter.name("bag").jsonValue(bag.toJson().toString());
            jsonWriter.endObject();
        } catch (IOException | SecurityException ignored) {
            throw new Exception("Unable to create a " + uuid + " file");
        }
    }
}

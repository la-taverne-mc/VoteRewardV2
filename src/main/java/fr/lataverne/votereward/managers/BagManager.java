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
import fr.lataverne.votereward.objects.Reward;
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

    private final HashMap<UUID, Bag> bags = new HashMap<>();

    @SuppressWarnings ("FieldNotUsedInToString")
    private final VoteReward plugin;

    public BagManager(VoteReward plugin) {
        this.plugin = plugin;
    }

    private static void writeBagOnFile(@NotNull UUID uuid, @NotNull Bag bag) {
        try (FileWriter fileWriter = new FileWriter(BagManager.BAG_FOLDER + "/" + uuid, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(fileWriter)
        ) {
            jsonWriter.beginObject();
            jsonWriter.name("owner").value(uuid.toString());
            jsonWriter.name("bag").jsonValue(bag.toJson().toString());
            jsonWriter.endObject();
        } catch (IOException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Unable to create a " + uuid + " file");
        } catch (SecurityException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "SecurityException: " + e.getMessage());
        }
    }

    public Map<UUID, Bag> getBags() {
        return Collections.unmodifiableMap(this.bags);
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

    public List<String> getOwnerNames() {
        return this.bags.keySet()
                .stream().map(Bukkit::getOfflinePlayer)
                .filter(offlinePlayer -> offlinePlayer != null && offlinePlayer.hasPlayedBefore())
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
    }

    private static @Nullable Pair<UUID, Bag> parseBagFile(File file) {
        if (file == null || !file.exists()) {
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

                Collection<Reward> content = new ArrayList<>();
                jsonBag.forEach(jsonReward -> content.add(Reward.parseJson(jsonReward)));

                return new ImmutablePair<>(owner, new Bag(content));
            }

            reader.close();
        } catch (FileNotFoundException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "The \"" + file.getPath() + "\" file wasn't found");
        } catch (IOException | IllegalArgumentException | NoSuchElementException e) {
            VoteReward.sendMessageToConsole(ChatColor.RED + "Error : " + e.getMessage());
        }

        return null;
    }

    public static void giveBag(@NotNull Bag bag, Player player, int maxNbRewardsRetrieving) {
        if (bag.getBagContent().isEmpty()) {
            Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.noRewardToBeRetrieved"));
            return;
        }

        int nbRewardsRetrieving = 0;
        int nbItemInBag = bag.getBagContent().size();

        boolean giveReward = BagManager.inventoryPlayerHasEmptySlot(player) && nbRewardsRetrieving < nbItemInBag && nbRewardsRetrieving < maxNbRewardsRetrieving;
        while (giveReward) {
            Reward reward = bag.getRandomReward();
            player.getInventory().addItem(new ItemStack(reward.itemStack()));
            bag.removeReward(reward);
            nbRewardsRetrieving++;

            giveReward = BagManager.inventoryPlayerHasEmptySlot(player) && nbRewardsRetrieving < nbItemInBag && nbRewardsRetrieving < maxNbRewardsRetrieving;
        }

        String message = Helper.replaceValueInString(Helper.getMessageOnConfig("player.retrieveRewards"), Integer.toString(nbRewardsRetrieving));
        Helper.sendMessageToPlayer(player, message);
    }

    private static boolean inventoryPlayerHasEmptySlot(@NotNull Player player) {
        ItemStack[] inventoryContent = player.getInventory().getContents();

        return IntStream.range(0, Constant.PLAYER_INVENTORY_SIZE).anyMatch(i -> inventoryContent[i] == null);
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

    public void saveBags() {
        for (Map.Entry<UUID, Bag> entry : this.bags.entrySet()) {
            UUID uuid = entry.getKey();
            Bag bag = entry.getValue();

            BagManager.writeBagOnFile(uuid, bag);
        }
    }

    public void saveBag(UUID uuid) {
        Bag bag = this.bags.get(uuid);

        if (bag != null) {
            BagManager.writeBagOnFile(uuid, bag);
        }
    }

    public void loadBags() {
        File bagsFolder = BagManager.getBagsFolder();

        if (bagsFolder == null) {
            return;
        }

        File[] files = bagsFolder.listFiles();

        if (files != null) {
            VoteReward.sendMessageToConsole(this.plugin.getConfig().getString("message.system.bagsLoading"));

            this.bags.clear();

            for (File file : files) {
                Pair<UUID, Bag> ownerAndBag = BagManager.parseBagFile(file);
                if (ownerAndBag != null) {
                    this.bags.put(ownerAndBag.getKey(), ownerAndBag.getValue());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "BagManager{" +
                "bags=" + this.bags +
                "}";
    }
}

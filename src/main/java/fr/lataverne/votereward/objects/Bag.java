package fr.lataverne.votereward.objects;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

public class Bag {
    private static final int PLAYER_INVENTORY_SIZE = 36;

    private static final HashMap<UUID, Bag> bags = new HashMap<>();

    private final ArrayList<Reward> bagContent;

    private final UUID owner;

    public Bag(final List<Reward> inventoryContent, final UUID owner) {
        this.bagContent = (ArrayList<Reward>) Collections.unmodifiableList(inventoryContent);
        this.owner = owner;
    }

    public static void clear() {
        Bag.bags.clear();
    }

    public static Bag[] getBags() {
        return Bag.bags.values().toArray(new Bag[0]);
    }

    public static Bag getPlayerBag(final UUID uuid) {
        Bag bag = Bag.bags.containsKey(uuid) ? Bag.bags.get(uuid) : Bag.createPlayerBag(uuid);
        bag.verifyExpirationDates();
        return bag;
    }

    public static void retrievingBag(final Bag bag, final Player player) {
        Bag.retrievingBag(bag, player, Constant.MAX_NB_REWARDS_RETRIEVING);
    }

    public static void retrievingBag(final Bag bag, final Player player, final int maxNbRewardsRetrieving) {

        if (bag.bagContent.isEmpty()) {
            Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.noRewardToBeRetrieved"));
            return;
        }

        int nbRewardsRetrieving = 0;
        int nbItemInBag = bag.bagContent.size();

        boolean cont = true;
        while (cont) {
            if (!Bag.inventoryPlayerIsFull(player) && nbRewardsRetrieving < nbItemInBag && nbRewardsRetrieving < maxNbRewardsRetrieving) {
                cont = false;
            } else {
                Reward reward = bag.getRandomReward();
                player.getInventory().addItem(new ItemStack(reward.itemStack()));
                bag.removeReward(reward);
                nbRewardsRetrieving++;
            }
        }

        String message = Helper.replaceValueInString(Helper.getMessageOnConfig("player.retrieveRewards"), Integer.toString(nbRewardsRetrieving));
        Helper.sendMessageToPlayer(player, message);
    }

    public void addNewReward(final Reward item) {
        this.bagContent.add(item);
    }

    public List<Reward> getBagContent() {
        return this.bagContent;
    }

    public @Nullable Reward getRandomReward() {
        if (this.bagContent.isEmpty()) {
            return null;
        } else {
            return this.bagContent.get(new SecureRandom().nextInt(this.bagContent.size()));
        }
    }

    public void removeReward(final Reward reward) {
        this.bagContent.remove(reward);
    }

    public void saveBag() {
        Path path = Paths.get(VoteReward.getInstance().getConfig().getString("system.bagsDirectory") + this.owner + ".yml");

        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (final IOException ignored) {
                String message = Helper.replaceValueInString(VoteReward.getInstance().getConfig().getString("message.system.deleteFileFailed"), path.toString());
                VoteReward.sendMessageToConsole(message);
            }
        }

        if (this.bagContent.isEmpty()) {
            return;
        }

        try {
            Files.createFile(path);

            int bagSize = this.bagContent.size();
            for (int i = 0; i < bagSize; i++) {
                Reward reward = this.bagContent.get(i);
                @NonNls String strReward = "reward_" + i + ":\n" + "  type: " + reward.itemStack().getType().name() + "\n" + "  amount: " + reward.itemStack().getAmount() + "\n" + "  ID: " + reward.achievableRewardId() + "\n" + "  expirationDate: " + reward.expirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n";

                Files.writeString(path, strReward, StandardOpenOption.APPEND);
            }

        } catch (final IOException ignored) {
        }
    }

    @Override
    public @NonNls String toString() {
        return "Bag{" + "bagContent=" + this.bagContent + ", owner=" + this.owner + '}';
    }

    private static Bag createPlayerBag(final UUID uuid) {
        Bag newBag = new Bag(new ArrayList<>(), uuid);
        Bag.bags.put(uuid, newBag);
        return newBag;
    }

    private static boolean inventoryPlayerIsFull(final Player player) {
        ItemStack[] inventoryContent = player.getInventory().getContents();

        return IntStream.range(0, Bag.PLAYER_INVENTORY_SIZE).noneMatch(i -> inventoryContent[i] == null) && inventoryContent[40] != null;
    }

    private void verifyExpirationDates() {
        if (!this.bagContent.isEmpty()) {
            this.bagContent.removeIf(reward -> reward.expirationDate().isBefore(LocalDate.now()));
        }
    }
}

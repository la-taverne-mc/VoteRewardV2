package fr.lataverne.votereward.objects;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;

public class Reward {
    private final LocalDate expiration;
    private final ItemStack itemStack;
    private final int achievableRewardId;

    public Reward(ItemStack itemStack, LocalDate expiration, int achievableRewardId) {
        this.expiration = expiration;
        this.itemStack = itemStack;
        this.achievableRewardId = achievableRewardId;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public LocalDate getExpirationDate() {
        return expiration;
    }

    public int getAchievableRewardId() {
        return achievableRewardId;
    }
}

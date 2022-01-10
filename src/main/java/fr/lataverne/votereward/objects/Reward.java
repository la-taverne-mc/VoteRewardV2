package fr.lataverne.votereward.objects;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;

public record Reward(ItemStack itemStack, LocalDate expirationDate, int achievableRewardId) {

}

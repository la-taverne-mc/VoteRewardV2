package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Constant;
import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.gui.BagView;
import fr.lataverne.votereward.gui.Gui;
import fr.lataverne.votereward.gui.RewardGroupStatsView;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandManager implements CommandExecutor {

	private final BagManager bagManager;

	private final GuiManager guiManager;

	private final RewardGroupManager rewardGroupManager;

	public CommandManager(BagManager bagManager, GuiManager guiManager, RewardGroupManager rewardGroupManager) {
		this.bagManager = bagManager;
		this.guiManager = guiManager;
		this.rewardGroupManager = rewardGroupManager;
	}

	private static void sendHelpPage(Player player, int page) {
		if (player != null) {
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage(ChatColor.RED + "Help command" + ChatColor.GRAY + " | Page " + page);
			player.sendMessage("");

			switch (page) {
				case 2 -> player.sendMessage(ChatColor.RED + "Voir l'aide" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv help " + ChatColor.GRAY + "[page]");
				case 1 -> {
					player.sendMessage(ChatColor.RED + "Voir son sac de récompense" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv bag see " + ChatColor.GRAY + "[page]");
					player.sendMessage(ChatColor.RED + "Récuperer son sac de récompense" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv bag get " + ChatColor.GRAY + "[amount]");
					player.sendMessage(ChatColor.RED + "Voir les récompenses disponibles" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv stat " + ChatColor.GRAY + "[page]");
					player.sendMessage(ChatColor.RED + "Générer un faux vote" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv fakevote " + ChatColor.GRAY + "[amount] " + ChatColor.GRAY + "[player]");
					player.sendMessage(ChatColor.RED + "Recharger le plugin" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv reload");
				}
			}

			player.sendMessage("");
			player.sendMessage(ChatColor.RED + "Help command" + ChatColor.GRAY + " | Page " + page);
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (sender instanceof Player player) {
			if (args.length < 1) {
				Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
				return true;
			}

			if ("bag".equalsIgnoreCase(args[0])) {
				if (args.length < 2) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
					return true;
				}

				if ("see".equalsIgnoreCase(args[1])) {
					if (Helper.playerHasPermission(player, "rv.player.bag.see")) {

						Bag bag = this.bagManager.getOrCreateBag(player.getUniqueId());
						BagView bagView = this.guiManager.getBagView(player, bag);

						player.openInventory(bagView.getInventory());
					}

					return true;
				} // rv bag see

				if ("get".equalsIgnoreCase(args[1])) {
					if (Helper.playerHasPermission(player, "rv.player.bag.get")) {
						int maxNbRewardsRetrieving = Constant.MAX_NB_REWARDS_RETRIEVING;

						if (args.length > 2) {
							try {
								maxNbRewardsRetrieving = Integer.parseInt(args[2]);
							} catch (NumberFormatException ignored) {

							}
						}

						Bag bag = this.bagManager.getOrCreateBag(player.getUniqueId());
						BagManager.giveBag(bag, player, maxNbRewardsRetrieving);
					} else {
						Helper.sendMessageToPlayer(player, Helper.replaceValueInString(Helper.getMessageOnConfig("player.notPermission"), "message.player.notPermission"));
					}

					return true;
				} // rv get [amount]
			} // rv bag ...

			if ("stat".equalsIgnoreCase(args[0])) {
				if (Helper.playerHasPermission(player, "rv.player.stat")) {
					int page = 0;

					if (args.length > 2) {
						try {
							page = Integer.parseInt(args[2]);
						} catch (NumberFormatException ignored) {

						}
					}

					RewardGroupStatsView rewardGroupStatsView = this.guiManager.getRewardGroupStatsView(player, page);
					player.openInventory(rewardGroupStatsView.getInventory());
				}

				return true;
			} // rv stat

			if ("admin".equalsIgnoreCase(args[0])) {
				return this.playerAdminCommands(player, args);
			} // rv admin ...

			if ("stop".equalsIgnoreCase(args[0])) {
				if (args.length < 2) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
					return true;
				}

				if ("see".equalsIgnoreCase(args[1])) {
					if (!Helper.playerHasPermission(player, "rv.admin.stop.see")) {
						return true;
					}

					boolean hasPermission = !InternalPermission.isActivate("rv.player.bag.see");

					if (InternalPermission.setInternalPermission("rv.player.bag.see", hasPermission)) {
						if (hasPermission) {
							Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureActivated");
						} else {
							Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureDisabled");
						}
					} else {
						Helper.sendMessageToPlayer(player, "admin.stopCommandFailed");
					}

					return true;
				} // rv stop see

				if ("get".equalsIgnoreCase(args[1])) {
					if (!Helper.playerHasPermission(player, "rv.admin.stop.get")) {
						return true;
					}

					boolean hasPermission = !InternalPermission.isActivate("rv.player.bag.get");

					if (InternalPermission.setInternalPermission("rv.player.bag.get", hasPermission)) {
						if (hasPermission) {
							Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureActivated");
						} else {
							Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureDisabled");
						}
					} else {
						Helper.sendMessageToPlayer(player, "admin.stopCommandFailed");
					}

					return true;
				} // rv stop get

				Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
				return true;
			} // rv stop ...

			if ("fakevote".equalsIgnoreCase(args[0])) {
				int nbReward = 1;
				UUID targetPlayerUUID = player.getUniqueId();

				if (args.length > 1) {
					try {
						nbReward = Integer.parseInt(args[1]);
					} catch (NumberFormatException ignored) {
					}
				}

				if (args.length > 2) {
					if (Helper.playerHasPermission(player, "rv.admin.fakevote.other")) {
						@SuppressWarnings ("deprecation")
						OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[2]);
						if (targetPlayer.hasPlayedBefore()) {
							targetPlayerUUID = targetPlayer.getUniqueId();
						} else {
							Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.hasNoPlayedBeforeInServer"));
							return true;
						}
					}
				} else {
					if (!Helper.playerHasPermission(player, "rv.admin.fakevote.me")) {
						return true;
					}
				}

				if (this.rewardGroupManager.getNumberOfAchievableRewards() == 0) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.noAchievableRewardsAvailable"));
					return true;
				}

				for (int i = 0; i < nbReward; i++) {
					Reward reward = this.rewardGroupManager.getRandomReward();

					if (reward != null) {
						Bag bag = this.bagManager.getOrCreateBag(targetPlayerUUID);
						bag.addReward(reward);
					}
				}

				String message = Helper.replaceValueInString(Helper.getMessageOnConfig("player.rewardsAddedInBag"), Integer.toString(nbReward));
				Helper.sendMessageToPlayer(player, message);

				return true;
			} // rv fakevote [amount] [player]

			if ("help".equalsIgnoreCase(args[0])) {
				int page = 0;

				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException ignored) {

					}
				}

				CommandManager.sendHelpPage(player, page);

				return true;
			} // rv help [page]

			if ("reload".equalsIgnoreCase(args[0])) {
				if (Helper.playerHasPermission(player, "rv.admin.reload")) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("admin.reloading"));

					VoteReward.getInstance().reloadConfig();

					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("admin.successfulReload"));
				}
			} // rv reload
		}
		return false;
	}

	private boolean playerAdminCommands(Player player, String @NotNull ... args) {

		if (args.length < 2) {
			Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
			return true;
		}

		if ("bag".equalsIgnoreCase(args[1])) {
			if (args.length < 3) {
				Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
				return true;
			}

			if ("see".equalsIgnoreCase(args[2])) {
				if (!Helper.playerHasPermission(player, "rv.admin.bag.see")) {
					return true;
				}

				if (args.length < 4) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
					return true;
				}

				@SuppressWarnings ("deprecation")
				OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[3]);
				if (!targetPlayer.hasPlayedBefore()) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.hasNoPlayedBeforeInServer"));
					return true;
				}

				Bag bag = this.bagManager.getOrCreateBag(targetPlayer.getUniqueId());
				Gui gui = this.guiManager.getBagView(player, bag);
				player.openInventory(gui.getInventory());
				return true;
			} // rv admin bag see [player]

			if ("get".equalsIgnoreCase(args[2])) {
				if (!Helper.playerHasPermission(player, "rv.admin.bag.get")) {
					return true;
				}

				if (args.length < 4) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
					return true;
				}

				@SuppressWarnings ("deprecation") OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[3]);
				if (!targetPlayer.hasPlayedBefore()) {
					Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.hasNoPlayedBeforeInServer"));
					return true;
				}

				Bag bag = this.bagManager.getOrCreateBag(targetPlayer.getUniqueId());
				BagManager.giveBag(bag, player, Constant.MAX_NB_REWARDS_RETRIEVING);

				return true;
			} // rv admin bag get [player]

			Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
			return true;
		} // rv admin bag ...

		return false;
	}
}

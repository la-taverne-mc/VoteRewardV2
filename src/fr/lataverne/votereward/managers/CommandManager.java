package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.objects.AchievableReward;
import fr.lataverne.votereward.objects.Bag;
import fr.lataverne.votereward.objects.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.UUID;

public class CommandManager implements CommandExecutor {
    @SuppressWarnings({"deprecation", "NullableProblems"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                return true;
            }

            if (args[0].equalsIgnoreCase("bag")) {
                if (args.length < 2) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                    return true;
                }

                if (args[1].equalsIgnoreCase("see")) {
                    if (Helper.playerHasPermission(player,"rv.player.bag.see")) {
                        int page = 0;

                        if (args.length > 2) {
                            try {
                                page = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ignored) {

                            }
                        }

                        player.openInventory(GUI.getGUI(player, GUI.ETypeGui.Bag, page));
                    }

                    return true;
                } // rv bag see [page]

                if (args[1].equalsIgnoreCase("get")) {
                    if (Helper.playerHasPermission(player, "rv.player.bag.get")) {
                        int maxNbRewardsRetrieving = 35;

                        if (args.length > 2) {
                            try {
                                maxNbRewardsRetrieving = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ignored) {

                            }
                        }

                        Bag.retrievingBag(Bag.getPlayerBag(player.getUniqueId()), player, maxNbRewardsRetrieving);
                    } else {
                        Helper.sendMessageToPlayer(player, Helper.replaceValueInString(Helper.getMessageOnConfig("player.notPermission"), "message.player.notPermission"));
                    }

                    return true;
                } // rv get [amount]
            } // rv bag ...

            if (args[0].equalsIgnoreCase("stat")) {
                if (Helper.playerHasPermission(player, "rv.player.stat")) {
                    int page = 0;

                    if (args.length > 2) {
                        try {
                            page = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {

                        }
                    }

                    player.openInventory(GUI.getGUI(player, GUI.ETypeGui.Stat, page));
                }

                return true;
            } // rv stat

            if (args[0].equalsIgnoreCase("admin")) {
                return playerAdminCommands(player, args);
            } // rv admin ...

            if (args[0].equalsIgnoreCase("stop")) {
                if (args.length < 2) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                    return true;
                }

                if (args[1].equalsIgnoreCase("see")) {

                    boolean value = InternalPermission.isActivate("rv.player.bag.see");

                    if (InternalPermission.setInternalPermission("rv.player.bag.see", !value)) {
                        if (!value) {
                            Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureActivated");
                        } else {
                            Helper.sendMessageToPlayer(player, "admin.stopCommandSucceeds.featureDisabled");
                        }
                    } else {
                        Helper.sendMessageToPlayer(player, "admin.stopCommandFailed");
                    }

                    return true;
                } // rv stop see

                if (args[1].equalsIgnoreCase("get")) {

                    boolean value = InternalPermission.isActivate("rv.player.bag.get");

                    if (InternalPermission.setInternalPermission("rv.player.bag.get", !value)) {
                        if (!value) {
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

            if (args[0].equalsIgnoreCase("fakevote")) {
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

                if (AchievableReward.getNumberOfAchievableRewards() == 0) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.noAchievableRewardsAvailable"));
                    return true;
                }

                for (int i = 0; i < nbReward; i++) {

                    LocalDate expirationDate = LocalDate.now().plusDays(15);
                    AchievableReward achievableReward = AchievableReward.getRandomReward();

                    if (achievableReward == null) {
                        continue;
                    }

                    Reward reward = new Reward(achievableReward.getItemStack().clone(), expirationDate, achievableReward.getId());
                    Bag.getPlayerBag(targetPlayerUUID).addNewReward(reward);
                }

                String message = Helper.replaceValueInString(Helper.getMessageOnConfig("player.rewardsAddedInBag"), Integer.toString(nbReward));
                Helper.sendMessageToPlayer(player, message);

                return true;
            } // rv fakevote [amount] [player]

            if (args[0].equalsIgnoreCase("help")) {
                int page = 0;

                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {

                    }
                }

                Helper.sendHelpPage(player, page);

                return true;
            } // rv help [page]

            if (args[0].equalsIgnoreCase("reload")) {
                if (Helper.playerHasPermission(player, "rv.admin.reload")) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("admin.reloading"));

                    VoteReward.getInstance().reloadConfig();

                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("admin.successfulReload"));
                }
            } // rv reload
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean playerAdminCommands(Player player, String... args) {

        if (args.length < 2) {
            Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
            return true;
        }

        if (args[1].equalsIgnoreCase("bag")) {
            if (args.length < 3) {
                Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                return true;
            }

            if (args[2].equalsIgnoreCase("see")) {
                if (args.length < 4) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                    return true;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[3]);
                if (!targetPlayer.hasPlayedBefore()) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.hasNoPlayedBeforeInServer"));
                    return true;
                }

                player.openInventory(GUI.getGUI(player, GUI.ETypeGui.Bag, 0, targetPlayer.getUniqueId()));
                return true;
            } // rv admin bag see [player]

            if (args[2].equalsIgnoreCase("get")) {
                if (args.length < 4) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
                    return true;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[3]);
                if (!targetPlayer.hasPlayedBefore()) {
                    Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.hasNoPlayedBeforeInServer"));
                    return true;
                }

                Bag.retrievingBag(Bag.getPlayerBag(targetPlayer.getUniqueId()), player);
                return true;
            } // rv admin bag get [player]

            Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.misuseCommand"));
            return true;
        } // rv admin bag ...

        return false;
    }
}
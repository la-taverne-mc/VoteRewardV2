package fr.lataverne.votereward;

import fr.lataverne.votereward.managers.InternalPermission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public abstract class Helper {

    public static String colorizeString(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll("§0|&0", ChatColor.BLACK + "")
                .replaceAll("§1|&1", ChatColor.DARK_BLUE + "")
                .replaceAll("§2|&2", ChatColor.DARK_GREEN + "")
                .replaceAll("§3|&3", ChatColor.DARK_AQUA + "")
                .replaceAll("§4|&4", ChatColor.DARK_RED + "")
                .replaceAll("§5|&5", ChatColor.DARK_PURPLE + "")
                .replaceAll("§6|&6", ChatColor.GOLD + "")
                .replaceAll("§7|&7", ChatColor.GRAY + "")
                .replaceAll("§8|&8", ChatColor.DARK_GRAY + "")
                .replaceAll("§9|&9", ChatColor.BLUE + "")
                .replaceAll("§a|&a", ChatColor.GREEN + "")
                .replaceAll("§b|&b", ChatColor.AQUA + "")
                .replaceAll("§c|&c", ChatColor.RED + "")
                .replaceAll("§d|&d", ChatColor.LIGHT_PURPLE + "")
                .replaceAll("§e|&e", ChatColor.YELLOW + "")
                .replaceAll("§f|&f", ChatColor.WHITE + "")
                .replaceAll("§k|&k", ChatColor.MAGIC + "")
                .replaceAll("§l|&l", ChatColor.BOLD + "")
                .replaceAll("§m|&m", ChatColor.STRIKETHROUGH + "")
                .replaceAll("§n|&n", ChatColor.UNDERLINE + "")
                .replaceAll("§o|&o", ChatColor.ITALIC + "")
                .replaceAll("§r|&r", ChatColor.RESET + "");
    }

    public static void sendMessageToPlayer(Player player, String message) {
        String suffixMessage = getMessageOnConfig("message.messageSuffix");
        player.sendMessage(Helper.colorizeString(suffixMessage + " &r" + message));
    }

    public static String getMessageOnConfig(String path) {
        if (!path.startsWith("message.")) {
            path = "message." + path;
        }

        return VoteReward.getInstance().getConfig().getString(path);
    }

    public static String replaceValueInString(String str, String... args) {
        for (int i = 0; i < args.length; i++) {
            str = str.replace("{" + (i + 1) + "}", args[i]);
        }

        return str;
    }

    public static boolean inventoryPlayerIsFull(Player player) {
        ItemStack[] inventoryContent = player.getInventory().getContents();

        return IntStream.range(0, 36).noneMatch(i -> inventoryContent[i] == null) && inventoryContent[40] != null;
    }

    public static boolean playerHasPermission(Player player, String permission) {
        if (player.hasPermission(permission)) {
            if (InternalPermission.isActivate(permission)) {
                return true;
            } else {
                Helper.sendMessageToPlayer(player, Helper.getMessageOnConfig("player.featureDisabled"));
                return false;
            }
        } else {
            Helper.sendMessageToPlayer(player, Helper.replaceValueInString(Helper.getMessageOnConfig("player.notPermission"), permission));
            return false;
        }
    }

    public static void sendHelpPage(Player player, int page) {
        if (player != null) {
            player.sendMessage("");
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "Help command" + ChatColor.GRAY + " | Page " + page);
            player.sendMessage("");

            switch (page) {
                default:
                case 1:
                    player.sendMessage(ChatColor.RED + "Voir son sac de récompense" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv bag see " + ChatColor.GRAY + "[page]");
                    player.sendMessage(ChatColor.RED + "Récuperer son sac de récompense" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv bag get " + ChatColor.GRAY + "[amount]");
                    player.sendMessage(ChatColor.RED + "Voir les récompenses disponibles" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv stat " + ChatColor.GRAY + "[page]");
                    player.sendMessage(ChatColor.RED + "Générer un faux vote" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv fakevote " + ChatColor.GRAY + "[amount] " + ChatColor.GRAY + "[player]");
                    player.sendMessage(ChatColor.RED + "Recharger le plugin" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv reload");
                    break;
                case 2:
                    player.sendMessage(ChatColor.RED + "Voir l'aide" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "/rv help " + ChatColor.GRAY + "[page]");
                    break;
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "Help command" + ChatColor.GRAY + " | Page " + page);
        }
    }
}

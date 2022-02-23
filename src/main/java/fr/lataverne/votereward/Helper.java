package fr.lataverne.votereward;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public enum Helper {
    ;

    private static final Pattern REGEX_CHATCOLOR_AQUA = Pattern.compile("§b|&b");

    private static final Pattern REGEX_CHATCOLOR_BLACK = Pattern.compile("§0|&0");

    private static final Pattern REGEX_CHATCOLOR_BLUE = Pattern.compile("§9|&9");

    private static final Pattern REGEX_CHATCOLOR_BOLD = Pattern.compile("§l|&l");

    private static final Pattern REGEX_CHATCOLOR_DARK_AQUA = Pattern.compile("§3|&3");

    private static final Pattern REGEX_CHATCOLOR_DARK_BLUE = Pattern.compile("§1|&1");

    private static final Pattern REGEX_CHATCOLOR_DARK_GRAY = Pattern.compile("§8|&8");

    private static final Pattern REGEX_CHATCOLOR_DARK_GREEN = Pattern.compile("§2|&2");

    private static final Pattern REGEX_CHATCOLOR_DARK_PURPLE = Pattern.compile("§5|&5");

    private static final Pattern REGEX_CHATCOLOR_DARK_RED = Pattern.compile("§4|&4");

    private static final Pattern REGEX_CHATCOLOR_GOLD = Pattern.compile("§6|&6");

    private static final Pattern REGEX_CHATCOLOR_GRAY = Pattern.compile("§7|&7");

    private static final Pattern REGEX_CHATCOLOR_GREEN = Pattern.compile("§a|&a");

    private static final Pattern REGEX_CHATCOLOR_ITALIC = Pattern.compile("§o|&o");

    private static final Pattern REGEX_CHATCOLOR_LIGHT_PURPLE = Pattern.compile("§d|&d");

    private static final Pattern REGEX_CHATCOLOR_MAGIC = Pattern.compile("§k|&k");

    private static final Pattern REGEX_CHATCOLOR_RED = Pattern.compile("§c|&c");

    private static final Pattern REGEX_CHATCOLOR_RESET = Pattern.compile("§r|&r");

    private static final Pattern REGEX_CHATCOLOR_STRIKETHROUGH = Pattern.compile("§m|&m");

    private static final Pattern REGEX_CHATCOLOR_UNDERLINE = Pattern.compile("§n|&n");

    private static final Pattern REGEX_CHATCOLOR_WHITE = Pattern.compile("§f|&f");

    private static final Pattern REGEX_CHATCOLOR_YELLOW = Pattern.compile("§e|&e");

    public static @Nullable String colorizeString(final String text) {
        if (text == null) {
            return null;
        }

        String output = text;

        output = Helper.REGEX_CHATCOLOR_BLACK.matcher(output).replaceAll(ChatColor.BLACK + "");
        output = Helper.REGEX_CHATCOLOR_DARK_BLUE.matcher(output).replaceAll(ChatColor.DARK_BLUE + "");
        output = Helper.REGEX_CHATCOLOR_DARK_GREEN.matcher(output).replaceAll(ChatColor.DARK_GREEN + "");
        output = Helper.REGEX_CHATCOLOR_DARK_AQUA.matcher(output).replaceAll(ChatColor.DARK_AQUA + "");
        output = Helper.REGEX_CHATCOLOR_DARK_RED.matcher(output).replaceAll(ChatColor.DARK_RED + "");
        output = Helper.REGEX_CHATCOLOR_DARK_PURPLE.matcher(output).replaceAll(ChatColor.DARK_PURPLE + "");
        output = Helper.REGEX_CHATCOLOR_GOLD.matcher(output).replaceAll(ChatColor.GOLD + "");
        output = Helper.REGEX_CHATCOLOR_GRAY.matcher(output).replaceAll(ChatColor.GRAY + "");
        output = Helper.REGEX_CHATCOLOR_DARK_GRAY.matcher(output).replaceAll(ChatColor.DARK_GRAY + "");
        output = Helper.REGEX_CHATCOLOR_BLUE.matcher(output).replaceAll(ChatColor.BLUE + "");
        output = Helper.REGEX_CHATCOLOR_GREEN.matcher(output).replaceAll(ChatColor.GREEN + "");
        output = Helper.REGEX_CHATCOLOR_AQUA.matcher(output).replaceAll(ChatColor.AQUA + "");
        output = Helper.REGEX_CHATCOLOR_RED.matcher(output).replaceAll(ChatColor.RED + "");
        output = Helper.REGEX_CHATCOLOR_LIGHT_PURPLE.matcher(output).replaceAll(ChatColor.LIGHT_PURPLE + "");
        output = Helper.REGEX_CHATCOLOR_YELLOW.matcher(output).replaceAll(ChatColor.YELLOW + "");
        output = Helper.REGEX_CHATCOLOR_WHITE.matcher(output).replaceAll(ChatColor.WHITE + "");
        output = Helper.REGEX_CHATCOLOR_MAGIC.matcher(output).replaceAll(ChatColor.MAGIC + "");
        output = Helper.REGEX_CHATCOLOR_BOLD.matcher(output).replaceAll(ChatColor.BOLD + "");
        output = Helper.REGEX_CHATCOLOR_STRIKETHROUGH.matcher(output).replaceAll(ChatColor.STRIKETHROUGH + "");
        output = Helper.REGEX_CHATCOLOR_UNDERLINE.matcher(output).replaceAll(ChatColor.UNDERLINE + "");
        output = Helper.REGEX_CHATCOLOR_ITALIC.matcher(output).replaceAll(ChatColor.ITALIC + "");
        output = Helper.REGEX_CHATCOLOR_RESET.matcher(output).replaceAll(ChatColor.RESET + "");

        return output;
    }

    public static String getMessageOnConfig(final String path) {
        String entirePath = path;
        if (!entirePath.startsWith("message.")) {
            entirePath = "message." + entirePath;
        }

        return VoteReward.getInstance().getConfig().getString(entirePath);
    }

    public static @Nullable String getStringInConfig(String path) {
        return VoteReward.getInstance().getConfig().getString(path);
    }

    @Contract(pure = true)
    public static String replaceValueInString(final String str, final String @NotNull ... args) {
        String output = str;
        int nbArg = args.length;

        for (int i = 0; i < nbArg; i++) {
            output = output.replace("{" + (i + 1) + "}", args[i]);
        }

        return output;
    }

    public static void sendMessageToPlayer(final @NotNull Player player, final String message) {
        String suffixMessage = Helper.getMessageOnConfig("message.messageSuffix");
        player.sendMessage(Helper.colorizeString(suffixMessage + " &r" + message));
    }
}

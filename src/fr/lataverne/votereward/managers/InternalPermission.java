package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;

public class InternalPermission {
    private static final HashMap<String, Boolean> internalPermissions = new HashMap<>();

    private static final String path = "system.internalPermission";

    public static void loadingInternalPermissions() {
        VoteReward.sendMessageToConsole(Helper.getMessageOnConfig("system.internalPermLoading"));

        ConfigurationSection internalPermList = VoteReward.getInstance().getConfig().getConfigurationSection(path);

        if (internalPermList != null) {
            String message = Helper.getMessageOnConfig("system.internalPermView");

            for (String key : internalPermList.getKeys(false)) {
                String permission = key.replace("-", ".");
                boolean value = VoteReward.getInstance().getConfig().getBoolean(path + "." + key);

                internalPermissions.put(permission, value);
                VoteReward.sendMessageToConsole(Helper.replaceValueInString(message, permission, Boolean.toString(value)));
            }
        }
    }

    public static boolean setInternalPermission(String permission, boolean value) {
        String pathPermission = path + permission.replace(".", "-");

        internalPermissions.put(permission, value);
        VoteReward.getInstance().getConfig().set(pathPermission, value);

        try {
            VoteReward.getInstance().getConfig().save("plugins/VoteReward/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isActivate(String permission) {
        return internalPermissions.getOrDefault(permission, true);
    }
}

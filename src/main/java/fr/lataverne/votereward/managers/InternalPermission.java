package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.Helper;
import fr.lataverne.votereward.VoteReward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InternalPermission {

    private static final Map<String, Boolean> internalPermissions = new HashMap<>();

    private static final @NonNls String path = "system.internalPermission";

    public static boolean isActivate(final String permission) {
        return InternalPermission.internalPermissions.getOrDefault(permission, true).booleanValue();
    }

    public static void loadingInternalPermissions() {
        VoteReward.sendMessageToConsole(Helper.getMessageOnConfig("system.internalPermLoading"));

        ConfigurationSection internalPermList = VoteReward.getInstance().getConfig()
                                                          .getConfigurationSection(InternalPermission.path);

        if (internalPermList != null) {
            String message = Helper.getMessageOnConfig("system.internalPermView");

            for (final String key : internalPermList.getKeys(false)) {
                String permission = key.replace("-", ".");
                boolean value = VoteReward.getInstance().getConfig().getBoolean(InternalPermission.path + "." + key);

                InternalPermission.internalPermissions.put(permission, value);
                VoteReward.sendMessageToConsole(Helper.replaceValueInString(message, permission, Boolean.toString(value)));
            }
        }
    }

    public static boolean setInternalPermission(final @NotNull String permission, final boolean value) {
        String pathPermission = InternalPermission.path + permission.replace(".", "-");

        InternalPermission.internalPermissions.put(permission, value);
        VoteReward.getInstance().getConfig().set(pathPermission, value);

        try {
            VoteReward.getInstance().getConfig().save("plugins\\VoteReward\\config.yml");
        } catch (final IOException e) {
            return false;
        }

        return true;
    }
}

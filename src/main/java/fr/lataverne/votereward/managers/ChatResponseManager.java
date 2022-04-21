package fr.lataverne.votereward.managers;

import fr.lataverne.votereward.VoteReward;
import fr.lataverne.votereward.utils.StringParameterizedRunnable;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatResponseManager {

    private final Map<UUID, Runnable> pendingProcessing = new HashMap<>();

    private final VoteReward plugin;

    public ChatResponseManager(VoteReward plugin) {

        this.plugin = plugin;
    }

    public void add(UUID uuid, Runnable processing) {
        this.pendingProcessing.put(uuid, processing);
    }

    public boolean awaitingResponse(UUID uuid) {
        return this.pendingProcessing.containsKey(uuid);
    }

    public void runProcessing(UUID uuid, Object param) {
        Runnable runnable = this.pendingProcessing.get(uuid);

        if (runnable instanceof StringParameterizedRunnable paramRunnable && param instanceof String str) {
            paramRunnable.setParameter(str);
        }

        Bukkit.getScheduler().runTask(this.plugin, runnable);

        this.pendingProcessing.remove(uuid);
    }
}

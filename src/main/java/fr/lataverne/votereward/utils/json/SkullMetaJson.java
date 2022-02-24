package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

enum SkullMetaJson {
    ;

    public static void deserialize(final @NotNull SkullMeta skullMeta, final @NotNull JsonElement elemSkullMeta) {
        if (elemSkullMeta.isJsonObject()) {
            JsonObject jsonSkullMeta = elemSkullMeta.getAsJsonObject();

            if (jsonSkullMeta.has("owner")) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(jsonSkullMeta.get("owner")
                                                                                               .getAsString())));
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull SkullMeta skullMeta) {
        if (skullMeta.hasOwner()) {
            JsonObject jsonSkullMeta = new JsonObject();
            jsonSkullMeta.addProperty("owner", skullMeta.getOwningPlayer().getUniqueId().toString());
            return jsonSkullMeta;
        } else {
            return null;
        }
    }
}

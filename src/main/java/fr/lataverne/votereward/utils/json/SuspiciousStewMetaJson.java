package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum SuspiciousStewMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull SuspiciousStewMeta suspiciousStewMeta) {
        if (suspiciousStewMeta.hasCustomEffects()) {
            JsonObject jsonSuspiciousStewMeta = new JsonObject();

            JsonArray potions = new JsonArray();
            suspiciousStewMeta.getCustomEffects().forEach(potionEffect -> potions.add(PotionEffectJson.serialize(potionEffect)));

            jsonSuspiciousStewMeta.add("customEffects", potions);
            return jsonSuspiciousStewMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull SuspiciousStewMeta suspiciousStewMeta, final @NotNull JsonElement elemSuspiciousStewMeta) {
        if (elemSuspiciousStewMeta.isJsonObject()) {
            JsonObject jsonSuspiciousStewMeta = elemSuspiciousStewMeta.getAsJsonObject();

            if (jsonSuspiciousStewMeta.has("customEffects")) {
                JsonArray jsonCustomEffects = jsonSuspiciousStewMeta.getAsJsonArray("customEffects");
                jsonCustomEffects.forEach(jsonCustomEffect -> suspiciousStewMeta.addCustomEffect(PotionEffectJson.deserialize(jsonCustomEffect), true));
            }
        }
    }
}

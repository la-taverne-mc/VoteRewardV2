package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum FireworkEffectMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull FireworkEffectMeta fireworkEffectMeta) {
        if (fireworkEffectMeta.hasEffect()) {
            JsonObject jsonFireworkEffectMeta = new JsonObject();

            FireworkEffect effect = fireworkEffectMeta.getEffect();
            jsonFireworkEffectMeta.add("effect", FireworkEffectJson.serialize(effect));

            return jsonFireworkEffectMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull FireworkEffectMeta fireworkEffectMeta, final @NotNull JsonElement elemFireworkEffectMeta) {
        if (elemFireworkEffectMeta.isJsonObject()) {
            JsonObject jsonFireworkEffectMeta = elemFireworkEffectMeta.getAsJsonObject();

            if (jsonFireworkEffectMeta.has("effect")) {
                JsonObject jsonEffect = jsonFireworkEffectMeta.getAsJsonObject("effect");
                fireworkEffectMeta.setEffect(FireworkEffectJson.deserialize(jsonEffect));
            }
        }
    }
}

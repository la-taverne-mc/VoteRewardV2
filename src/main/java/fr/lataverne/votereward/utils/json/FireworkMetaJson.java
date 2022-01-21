package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

enum FireworkMetaJson {
    ;

    public static @NotNull JsonObject serialize(final @NotNull FireworkMeta fireworkMeta) {
        JsonObject jsonFireworkMeta = new JsonObject();

        jsonFireworkMeta.addProperty("power", fireworkMeta.getPower());

        if (fireworkMeta.hasEffects()) {
            JsonArray effects = new JsonArray();
            fireworkMeta.getEffects().forEach(effect -> effects.add(FireworkEffectJson.serialize(effect)));
            jsonFireworkMeta.add("effects", effects);
        }

        return jsonFireworkMeta;
    }

    public static void deserialize(final @NotNull FireworkMeta fireworkMeta, final @NotNull JsonElement elemFireworkMeta) {
        if (elemFireworkMeta.isJsonObject()) {
            JsonObject jsonFireworkMeta = elemFireworkMeta.getAsJsonObject();

            if (jsonFireworkMeta.has("power")) {
                fireworkMeta.setPower(jsonFireworkMeta.get("power").getAsInt());
            }

            if (jsonFireworkMeta.has("effects")) {
                JsonArray jsonEffects = jsonFireworkMeta.getAsJsonArray("effects");
                jsonEffects.forEach(jsonEffect -> fireworkMeta.addEffect(FireworkEffectJson.deserialize(jsonEffect)));
            }
        }
    }
}

package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum FireworkEffectJson {
    ;

    public static @Nullable FireworkEffect deserialize(final @NotNull JsonElement elemFireworkEffect) {
        if (elemFireworkEffect.isJsonObject()) {
            JsonObject jsonFireworkEffect = elemFireworkEffect.getAsJsonObject();

            if (jsonFireworkEffect.has("type")) {
                FireworkEffect.Builder fireworkEffectBuilder = FireworkEffect.builder();

                FireworkEffect.Type type = FireworkEffect.Type.valueOf(jsonFireworkEffect.get("type").getAsString());

                fireworkEffectBuilder.with(type);

                if (jsonFireworkEffect.has("flicker")) {
                    fireworkEffectBuilder.flicker(jsonFireworkEffect.get("flicker").getAsBoolean());
                }

                if (jsonFireworkEffect.has("trail")) {
                    fireworkEffectBuilder.trail(jsonFireworkEffect.get("trail").getAsBoolean());
                }

                if (jsonFireworkEffect.has("colors")) {
                    JsonArray jsonColors = jsonFireworkEffect.getAsJsonArray("colors");
                    jsonColors.forEach(jsonColor -> fireworkEffectBuilder.withColor(Color.fromRGB(jsonColor.getAsInt())));
                }

                if (jsonFireworkEffect.has("fadeColors")) {
                    JsonArray jsonFadeColors = jsonFireworkEffect.getAsJsonArray("fadeColors");
                    jsonFadeColors.forEach(jsonFadeColor -> fireworkEffectBuilder.withFade(Color.fromRGB(jsonFadeColor.getAsInt())));
                }

                return fireworkEffectBuilder.build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static @NotNull JsonObject serialize(final @NotNull FireworkEffect fireworkEffect) {
        JsonObject jsonFireworkEffect = new JsonObject();
        jsonFireworkEffect.addProperty("type", fireworkEffect.getType().name());

        if (fireworkEffect.hasFlicker()) {
            jsonFireworkEffect.addProperty("flicker", true);
        }

        if (fireworkEffect.hasTrail()) {
            jsonFireworkEffect.addProperty("trail", true);
        }

        if (!fireworkEffect.getColors().isEmpty()) {
            JsonArray colors = new JsonArray();
            fireworkEffect.getColors().forEach(color -> colors.add(new JsonPrimitive(color.asRGB())));
            jsonFireworkEffect.add("colors", colors);
        }

        if (!fireworkEffect.getFadeColors().isEmpty()) {
            JsonArray fadeColors = new JsonArray();
            fireworkEffect.getFadeColors().forEach(color -> fadeColors.add(new JsonPrimitive(color.asRGB())));
            jsonFireworkEffect.add("fadeColors", fadeColors);
        }

        return jsonFireworkEffect;
    }
}

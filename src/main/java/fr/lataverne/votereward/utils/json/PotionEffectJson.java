package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum PotionEffectJson {
    ;

    public static @Nullable PotionEffect deserialize(final @NotNull JsonElement elemPotionEffect) {
        if (elemPotionEffect.isJsonObject()) {
            JsonObject jsonPotionEffect = elemPotionEffect.getAsJsonObject();

            if (jsonPotionEffect.has("type") && jsonPotionEffect.has("amplifier") && jsonPotionEffect.has("duration") &&
                jsonPotionEffect.has("ambient") && jsonPotionEffect.has("icon") && jsonPotionEffect.has("particles")) {
                PotionEffectType type = PotionEffectType.getByName(jsonPotionEffect.get("type").getAsString());
                int amplifier = jsonPotionEffect.get("amplifier").getAsInt();
                int duration = jsonPotionEffect.get("duration").getAsInt();
                boolean ambient = jsonPotionEffect.get("ambient").getAsBoolean();
                boolean icon = jsonPotionEffect.get("icon").getAsBoolean();
                boolean particles = jsonPotionEffect.get("particles").getAsBoolean();

                return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static @NotNull JsonObject serialize(final @NotNull PotionEffect potionEffect) {
        JsonObject jsonPotionEffect = new JsonObject();

        jsonPotionEffect.addProperty("type", potionEffect.getType().getName());
        jsonPotionEffect.addProperty("amplifier", potionEffect.getAmplifier());
        jsonPotionEffect.addProperty("duration", potionEffect.getDuration());
        jsonPotionEffect.addProperty("ambient", potionEffect.isAmbient());
        jsonPotionEffect.addProperty("icon", potionEffect.hasIcon());
        jsonPotionEffect.addProperty("particles", potionEffect.hasParticles());

        return jsonPotionEffect;
    }
}

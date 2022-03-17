package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum PotionMetaJson {
    ;

    public static void deserialize(final @NotNull PotionMeta potionMeta, final @NotNull JsonElement elemPotionMeta) {
        if (elemPotionMeta.isJsonObject()) {
            JsonObject jsonPotionMeta = elemPotionMeta.getAsJsonObject();

            if (jsonPotionMeta.has("customEffects")) {
                JsonArray jsonPotionEffects = jsonPotionMeta.getAsJsonArray("customEffects");
                jsonPotionEffects.forEach(jsonPotionEffect -> potionMeta.addCustomEffect(PotionEffectJson.deserialize(jsonPotionEffect), true));
            } else if (jsonPotionMeta.has("baseEffect")) {
                JsonObject jsonBaseEffect = jsonPotionMeta.getAsJsonObject("baseEffect");

                PotionType type = PotionType.UNCRAFTABLE;
                boolean isExtended = false;
                boolean isUpgraded = false;

                if (jsonBaseEffect.has("type")) {
                    type = PotionType.valueOf(jsonBaseEffect.get("type").getAsString());
                }

                if (jsonBaseEffect.has("isExtended")) {
                    isExtended = jsonBaseEffect.get("isExtended").getAsBoolean();
                }

                if (jsonBaseEffect.has("isUpgraded")) {
                    isUpgraded = jsonBaseEffect.get("isUpgraded").getAsBoolean();
                }

                potionMeta.setBasePotionData(new PotionData(type, isExtended, isUpgraded));
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull PotionMeta potionMeta) {
        JsonObject jsonPotionMeta = new JsonObject();

        if (potionMeta.hasCustomEffects()) {
            JsonArray customEffects = new JsonArray();
            potionMeta.getCustomEffects()
                      .forEach(potionEffect -> customEffects.add(PotionEffectJson.serialize(potionEffect)));

            jsonPotionMeta.add("customEffects", customEffects);
            return jsonPotionMeta;
        } else {
            PotionType type = potionMeta.getBasePotionData().getType();
            boolean isExtended = potionMeta.getBasePotionData().isExtended();
            boolean isUpgraded = potionMeta.getBasePotionData().isUpgraded();

            JsonObject jsonBaseEffect = new JsonObject();
            if (type != null) {
                PotionEffectType effectType = type.getEffectType();
                if (effectType != null) {
                    jsonBaseEffect.addProperty("type", effectType.getName());
                }

                jsonBaseEffect.addProperty("isExtended", isExtended);
                jsonBaseEffect.addProperty("isUpgraded", isUpgraded);
                jsonPotionMeta.add("baseEffect", jsonBaseEffect);

                return jsonPotionMeta;
            } else {
                return null;
            }
        }
    }
}

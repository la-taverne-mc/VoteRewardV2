package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum TropicalFishBucketMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull TropicalFishBucketMeta tropicalFishBucketMeta) {
        if (tropicalFishBucketMeta.hasVariant()) {
            JsonObject jsonTFBMeta = new JsonObject();
            jsonTFBMeta.addProperty("bodyColor", tropicalFishBucketMeta.getBodyColor().name());
            jsonTFBMeta.addProperty("pattern", tropicalFishBucketMeta.getPattern().name());
            jsonTFBMeta.addProperty("patternColor", tropicalFishBucketMeta.getPatternColor().name());
            return jsonTFBMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull TropicalFishBucketMeta tropicalFishBucketMeta, final @NotNull JsonElement elemTFBMeta) {
        if (elemTFBMeta.isJsonObject()) {
            JsonObject jsonTFBMeta = elemTFBMeta.getAsJsonObject();

            if (jsonTFBMeta.has("bodyColor")) {
                tropicalFishBucketMeta.setBodyColor(DyeColor.valueOf(jsonTFBMeta.get("bodyColor").getAsString()));
            }

            if (jsonTFBMeta.has("pattern")) {
                tropicalFishBucketMeta.setPattern(TropicalFish.Pattern.valueOf(jsonTFBMeta.get("pattern").getAsString()));
            }

            if (jsonTFBMeta.has("patternColor")) {
                tropicalFishBucketMeta.setPatternColor(DyeColor.valueOf(jsonTFBMeta.get("patternColor").getAsString()));
            }
        }
    }
}

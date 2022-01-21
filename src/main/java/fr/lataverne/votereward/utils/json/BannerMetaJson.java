package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum BannerMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull BannerMeta bannerMeta) {
        if (bannerMeta.numberOfPatterns() > 0) {
            JsonObject jsonBannerMeta = new JsonObject();

            JsonArray patterns = new JsonArray();
            bannerMeta.getPatterns().forEach(pattern -> {
                JsonObject jsonPattern = new JsonObject();
                jsonPattern.addProperty("patternType", pattern.getPattern().getIdentifier());
                jsonPattern.addProperty("color", pattern.getColor().name());
                patterns.add(jsonPattern);
            });

            jsonBannerMeta.add("patterns", patterns);
            return jsonBannerMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull BannerMeta bannerMeta, final @NotNull JsonElement elemBannerMeta) {
        if (elemBannerMeta.isJsonObject()) {
            JsonObject jsonBannerMeta = elemBannerMeta.getAsJsonObject();

            if (jsonBannerMeta.has("patterns")) {
                JsonElement elemPatterns = jsonBannerMeta.get("patterns");

                if (elemPatterns.isJsonArray()) {
                    JsonArray jsonPatterns = elemPatterns.getAsJsonArray();

                    jsonPatterns.forEach(elemPattern -> {
                        Pattern pattern = BannerMetaJson.deserializePattern(elemPattern);
                        if (pattern != null) {
                            bannerMeta.addPattern(pattern);
                        }
                    });
                }
            }
        }
    }

    private static @Nullable Pattern deserializePattern(final @NotNull JsonElement elemPattern) {
        if (elemPattern.isJsonObject()) {
            JsonObject jsonPattern = elemPattern.getAsJsonObject();

            if (jsonPattern.has("color") && jsonPattern.has("patternType")) {
                DyeColor color = DyeColor.valueOf(jsonPattern.get("color").getAsString());
                PatternType patternType = PatternType.getByIdentifier(jsonPattern.get("patternType").getAsString());

                return new Pattern(color, patternType);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

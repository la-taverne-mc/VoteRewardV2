package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum AxolotlBucketMetaJson {
    ;

    public static void deserialize(final @NotNull AxolotlBucketMeta axolotlBucketMeta, final @NotNull JsonElement elemAxolotlBucketMeta) {
        if (elemAxolotlBucketMeta.isJsonObject()) {
            JsonObject jsonAxolotlBucketMeta = elemAxolotlBucketMeta.getAsJsonObject();

            if (jsonAxolotlBucketMeta.has("variant")) {
                try {
                    axolotlBucketMeta.setVariant(Axolotl.Variant.valueOf(jsonAxolotlBucketMeta.get("variant")
                                                                                              .getAsString()));
                } catch (final IllegalArgumentException ignored) {
                }
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull AxolotlBucketMeta axolotlBucketMeta) {
        if (axolotlBucketMeta.hasVariant()) {
            JsonObject jsonAxolotlBucketMeta = new JsonObject();
            jsonAxolotlBucketMeta.addProperty("variant", axolotlBucketMeta.getVariant().name());
            return jsonAxolotlBucketMeta;
        } else {
            return null;
        }
    }
}

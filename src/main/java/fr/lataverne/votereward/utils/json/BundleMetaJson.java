package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum BundleMetaJson {
    ;

    public static void deserialize(final @NotNull BundleMeta bundleMeta, final @NotNull JsonElement elemBundleMeta) {
        if (elemBundleMeta.isJsonObject()) {
            JsonObject jsonBundleMeta = elemBundleMeta.getAsJsonObject();

            if (jsonBundleMeta.has("items")) {
                JsonArray jsonItems = jsonBundleMeta.getAsJsonArray("items");

                jsonItems.forEach(jsonItem -> {
                    ItemStack item = ItemStackJson.deserialize(jsonItem);
                    if (item != null) {
                        bundleMeta.addItem(item);
                    }
                });
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull BundleMeta bundleMeta) {
        if (bundleMeta.hasItems()) {
            JsonObject jsonBundleMeta = new JsonObject();

            JsonArray items = new JsonArray();
            bundleMeta.getItems().forEach(itemStack -> items.add(ItemStackJson.serialize(itemStack)));

            jsonBundleMeta.add("items", items);
            return jsonBundleMeta;
        } else {
            return null;
        }
    }
}

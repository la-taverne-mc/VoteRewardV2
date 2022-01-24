package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum CrossbowMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull CrossbowMeta crossbowMeta) {
        if (crossbowMeta.hasChargedProjectiles()) {
            JsonObject jsonCrossbowMeta = new JsonObject();

            JsonArray chargedProjectiles = new JsonArray();
            crossbowMeta.getChargedProjectiles().forEach(itemStack -> chargedProjectiles.add(ItemStackJson.serialize(itemStack)));

            jsonCrossbowMeta.add("chargedProjectiles", chargedProjectiles);
            return jsonCrossbowMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull CrossbowMeta crossbowMeta, final @NotNull JsonElement elemCrossbowMeta) {
        if (elemCrossbowMeta.isJsonObject()) {
            JsonObject jsonCrossbowMeta = elemCrossbowMeta.getAsJsonObject();

            if (jsonCrossbowMeta.has("chargedProjectiles")) {
                JsonArray jsonChargedProjectiles = jsonCrossbowMeta.getAsJsonArray("chargedProjectiles");
                jsonChargedProjectiles.forEach(elemItem -> {
                    ItemStack chargedProjectile = ItemStackJson.deserialize(elemItem);
                    if (chargedProjectile != null) {
                        crossbowMeta.addChargedProjectile(chargedProjectile);
                    }
                });
            }
        }
    }
}

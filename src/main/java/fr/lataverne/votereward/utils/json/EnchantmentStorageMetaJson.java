package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum EnchantmentStorageMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull EnchantmentStorageMeta esMeta) {
        if (esMeta.hasStoredEnchants()) {
            JsonObject jsonESMeta = new JsonObject();

            JsonArray storedEnchants = new JsonArray();
            esMeta.getStoredEnchants().forEach((enchantment, level) -> {
                JsonObject enchantmentJson = new JsonObject();
                enchantmentJson.addProperty("key", enchantment.getKey().toString());
                enchantmentJson.addProperty("level", level);
                storedEnchants.add(enchantmentJson);
            });
            jsonESMeta.add("storedEnchants", storedEnchants);

            return jsonESMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull EnchantmentStorageMeta esMeta, final @NotNull JsonElement elemESMeta) {
        if (elemESMeta.isJsonObject()) {
            JsonObject jsonESMeta = elemESMeta.getAsJsonObject();

            if (jsonESMeta.has("storedEnchants")) {
                JsonArray jsonStoredEnchants = jsonESMeta.getAsJsonArray("storedEnchants");
                jsonStoredEnchants.forEach(elemStoredEnchant -> EnchantmentStorageMetaJson.addJsonEnchantmentToEnchantmentStorageMeta(esMeta, elemStoredEnchant));
            }
        }
    }

    private static void addJsonEnchantmentToEnchantmentStorageMeta(final @NotNull EnchantmentStorageMeta esMeta, final @NotNull JsonElement elemStoredEnchant) {
        if (elemStoredEnchant.isJsonObject()) {
            JsonObject jsonStoredEnchant = elemStoredEnchant.getAsJsonObject();

            if (jsonStoredEnchant.has("key") && jsonStoredEnchant.has("level")) {
                NamespacedKey key = NamespacedKey.fromString(jsonStoredEnchant.get("key").getAsString());
                int level = jsonStoredEnchant.get("level").getAsInt();

                esMeta.addStoredEnchant(Enchantment.getByKey(key), level, true);
            }
        }
    }
}

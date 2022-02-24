package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum DamageableJson {
    ;

    public static void deserialize(final @NotNull Damageable damageable, final @NotNull JsonElement elemDamageable) {
        if (elemDamageable.isJsonObject()) {
            JsonObject jsonDamageable = elemDamageable.getAsJsonObject();

            if (jsonDamageable.has("damage")) {
                damageable.setDamage(jsonDamageable.get("damage").getAsInt());
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull Damageable damageable) {
        if (damageable.hasDamage()) {
            JsonObject jsonDamageable = new JsonObject();
            jsonDamageable.addProperty("damage", damageable.getDamage());
            return jsonDamageable;
        } else {
            return null;
        }
    }
}

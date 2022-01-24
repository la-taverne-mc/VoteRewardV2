package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

enum LeatherArmorMetaJson {
    ;

    public static @NotNull JsonObject serialize(final @NotNull LeatherArmorMeta leatherArmorMeta) {
        JsonObject jsonLeatherArmorMeta = new JsonObject();
        jsonLeatherArmorMeta.addProperty("color", leatherArmorMeta.getColor().asRGB());
        return jsonLeatherArmorMeta;
    }

    public static void deserialize(final @NotNull LeatherArmorMeta leatherArmorMeta, final @NotNull JsonElement elemLeatherArmorMeta) {
        if (elemLeatherArmorMeta.isJsonObject()) {
            JsonObject jsonLeatherArmorMeta = elemLeatherArmorMeta.getAsJsonObject();

            if (jsonLeatherArmorMeta.has("color")) {
                leatherArmorMeta.setColor(Color.fromRGB(jsonLeatherArmorMeta.get("color").getAsInt()));
            }
        }
    }
}

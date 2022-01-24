package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Color;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;

enum MapMetaJson {
    ;

    public static @NotNull JsonObject serialize(final @NotNull MapMeta mapMeta) {
        JsonObject jsonMapMeta = new JsonObject();

        jsonMapMeta.addProperty("scaling", mapMeta.isScaling());

        if (mapMeta.hasLocationName()) {
            jsonMapMeta.addProperty("locationName", mapMeta.getLocationName());
        }

        if (mapMeta.hasColor()) {
            jsonMapMeta.addProperty("color", mapMeta.getColor().asRGB());
        }

        return jsonMapMeta;
    }

    public static void deserialize(final @NotNull MapMeta mapMeta, final @NotNull JsonElement elemMapMeta) {
        if (elemMapMeta.isJsonObject()) {
            JsonObject jsonMapMeta = elemMapMeta.getAsJsonObject();

            if (jsonMapMeta.has("scaling")) {
                mapMeta.setScaling(jsonMapMeta.get("scaling").getAsBoolean());
            }

            if (jsonMapMeta.has("locationName")) {
                mapMeta.setLocationName(jsonMapMeta.get("locationName").getAsString());
            }

            if (jsonMapMeta.has("color")) {
                mapMeta.setColor(Color.fromBGR(jsonMapMeta.get("color").getAsInt()));
            }
        }
    }
}

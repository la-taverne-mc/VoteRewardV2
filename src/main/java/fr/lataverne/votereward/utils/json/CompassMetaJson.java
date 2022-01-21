package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

enum CompassMetaJson {
    ;

    public static @NotNull JsonObject serialize(final @NotNull CompassMeta compassMeta) {
        JsonObject jsonCompassMeta = new JsonObject();
        jsonCompassMeta.addProperty("isLodestoneTracked", compassMeta.isLodestoneTracked());

        if (compassMeta.hasLodestone()) {
            JsonObject jsonLodestone = new JsonObject();
            jsonLodestone.addProperty("world", compassMeta.getLodestone().getWorld().getUID().toString());
            jsonLodestone.addProperty("x", compassMeta.getLodestone().getX());
            jsonLodestone.addProperty("y", compassMeta.getLodestone().getY());
            jsonLodestone.addProperty("z", compassMeta.getLodestone().getZ());
            jsonCompassMeta.add("lodestone", jsonLodestone);
        }

        return jsonCompassMeta;
    }

    public static void deserialize(final @NotNull CompassMeta compassMeta, final @NotNull JsonElement elemCompassMeta) {
        if (elemCompassMeta.isJsonObject()) {
            JsonObject jsonCompassMeta = elemCompassMeta.getAsJsonObject();

            if (jsonCompassMeta.has("isLodestoneTracked")) {
                compassMeta.setLodestoneTracked(jsonCompassMeta.get("isLodestoneTracked").getAsBoolean());
            }

            if (jsonCompassMeta.has("lodestone")) {
                JsonElement elemLodestone = jsonCompassMeta.get("lodestone");

                if (elemCompassMeta.isJsonObject()) {
                    JsonObject jsonLodestone = elemLodestone.getAsJsonObject();

                    if (jsonLodestone.has("x") && jsonLodestone.has("y") && jsonLodestone.has("z") && jsonLodestone.has("world")) {
                        double x = jsonLodestone.get("x").getAsDouble();
                        double y = jsonLodestone.get("y").getAsDouble();
                        double z = jsonLodestone.get("z").getAsDouble();
                        World world = Bukkit.getWorld(UUID.fromString(jsonLodestone.get("world").getAsString()));

                        if (world != null) {
                            compassMeta.setLodestone(new Location(world, x, y, z));
                        }
                    }
                }
            }
        }
    }
}

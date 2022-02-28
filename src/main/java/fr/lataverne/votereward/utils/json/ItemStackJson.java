package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ItemStackJson {
    ;

    public static @Nullable ItemStack deserialize(final @NotNull JsonElement elemItemStack) {
        if (elemItemStack.isJsonObject()) {
            JsonObject jsonItem = elemItemStack.getAsJsonObject();

            JsonElement jsonType = jsonItem.get("type");
            if (jsonType.isJsonPrimitive()) {
                String type = jsonType.getAsString();
                int amount = jsonItem.has("amount")
                             ? jsonItem.get("amount").getAsInt()
                             : 1;

                ItemStack itemStack = new ItemStack(Material.getMaterial(type), amount);

                if (jsonItem.has("itemMeta")) {
                    ItemMeta itemMeta = ItemMetaJson.deserialize(itemStack.getItemMeta(), jsonItem.get("itemMeta"));
                    if (itemMeta != null) {
                        itemStack.setItemMeta(itemMeta);
                    }
                }

                return itemStack;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static @NotNull JsonObject serialize(final @NotNull ItemStack itemStack) {
        JsonObject itemJson = new JsonObject();

        itemJson.addProperty("type", itemStack.getType().name());

        itemJson.addProperty("amount", itemStack.getAmount());

        if (itemStack.hasItemMeta()) {
            JsonObject jsonItemMeta = ItemMetaJson.serialize(itemStack.getItemMeta());
            if (jsonItemMeta.size() > 0) {
                itemJson.add("itemMeta", jsonItemMeta);
            }
        }

        return itemJson;
    }
}

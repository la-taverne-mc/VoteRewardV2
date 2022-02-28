package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum BookMetaJson {
    ;

    public static void deserialize(final @NotNull BookMeta bookMeta, final @NotNull JsonElement elemBookMeta) {
        if (elemBookMeta.isJsonObject()) {
            JsonObject jsonBookMeta = elemBookMeta.getAsJsonObject();

            if (jsonBookMeta.has("title")) {
                bookMeta.setTitle(jsonBookMeta.get("title").getAsString());
            }

            if (jsonBookMeta.has("author")) {
                bookMeta.setAuthor(jsonBookMeta.get("author").getAsString());
            }

            if (jsonBookMeta.has("pages")) {
                JsonArray jsonPages = jsonBookMeta.getAsJsonArray("pages");

                jsonPages.forEach(jsonPage -> {
                    if (jsonPage.isJsonPrimitive()) {
                        bookMeta.addPage(jsonPage.getAsString());
                    }
                });
            }
        }
    }

    public static @Nullable JsonObject serialize(final @NotNull BookMeta bookMeta) {
        JsonObject jsonBookMeta = new JsonObject();

        if (bookMeta.hasTitle()) {
            jsonBookMeta.addProperty("title", bookMeta.getTitle());
        }

        if (bookMeta.hasAuthor()) {
            jsonBookMeta.addProperty("author", bookMeta.getAuthor());
        }

        if (bookMeta.hasPages()) {
            JsonArray pages = new JsonArray();
            bookMeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
            jsonBookMeta.add("pages", pages);
        }

        return jsonBookMeta.size() > 0
               ? jsonBookMeta
               : null;
    }
}

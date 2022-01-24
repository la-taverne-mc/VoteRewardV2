package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum KnowledgeBookMetaJson {
    ;

    public static @Nullable JsonObject serialize(final @NotNull KnowledgeBookMeta knowledgeBookMeta) {
        if (knowledgeBookMeta.hasRecipes()) {
            JsonObject jsonKnowledgeBookMeta = new JsonObject();

            JsonArray recipes = new JsonArray();
            knowledgeBookMeta.getRecipes().forEach(recipe -> recipes.add(recipe.getKey()));

            jsonKnowledgeBookMeta.add("recipes", recipes);
            return jsonKnowledgeBookMeta;
        } else {
            return null;
        }
    }

    public static void deserialize(final @NotNull KnowledgeBookMeta knowledgeBookMeta, final @NotNull JsonElement elemKnowledgeBookMeta) {
        if (elemKnowledgeBookMeta.isJsonObject()) {
            JsonObject jsonKnowledgeBookMeta = elemKnowledgeBookMeta.getAsJsonObject();

            if (jsonKnowledgeBookMeta.has("recipes")) {
                JsonArray jsonRecipes = jsonKnowledgeBookMeta.getAsJsonArray("recipes");
                jsonRecipes.forEach(jsonRecipe -> knowledgeBookMeta.addRecipe(NamespacedKey.fromString(jsonRecipe.getAsString())));
            }
        }
    }
}

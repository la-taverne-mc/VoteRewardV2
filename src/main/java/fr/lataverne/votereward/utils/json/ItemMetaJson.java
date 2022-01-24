package fr.lataverne.votereward.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings ("OverlyComplexClass")
enum ItemMetaJson {
    ;

    public static @NotNull JsonObject serialize(final @NotNull ItemMeta itemMeta) {
        JsonObject jsonItemMeta = new JsonObject();

        if (itemMeta.hasDisplayName()) {
            jsonItemMeta.addProperty("displayName", itemMeta.getDisplayName());
        }

        if (itemMeta.hasLore()) {
            JsonArray lore = new JsonArray();
            itemMeta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
            jsonItemMeta.add("lore", lore);
        }

        if (itemMeta.hasEnchants()) {
            JsonArray enchants = new JsonArray();
            itemMeta.getEnchants().forEach(((enchantment, level) -> {
                JsonObject jsonEnchantment = new JsonObject();
                jsonEnchantment.addProperty("key", enchantment.getKey().toString());
                jsonEnchantment.addProperty("level", level);
                enchants.add(jsonEnchantment);
            }));
            jsonItemMeta.add("enchants", enchants);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            JsonArray flags = new JsonArray();
            itemMeta.getItemFlags().stream().map(ItemFlag::name).forEach(flag -> flags.add(new JsonPrimitive(flag)));
            jsonItemMeta.add("flags", flags);
        }

        if (itemMeta.isUnbreakable()) {
            jsonItemMeta.addProperty("unbreakable", true);
        }

        if (itemMeta.hasCustomModelData()) {
            jsonItemMeta.addProperty("customModelData", itemMeta.getCustomModelData());
        }

        JsonObject extraMeta = ItemMetaJson.serializeExtraMeta(itemMeta);
        if (extraMeta != null && extraMeta.size() > 0) {
            jsonItemMeta.add("extra", extraMeta);
        }

        return jsonItemMeta;
    }

    @SuppressWarnings ("OverlyComplexMethod")
    public static @Nullable ItemMeta deserialize(@NotNull final ItemMeta itemMeta, final @NotNull JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject jsonMeta = element.getAsJsonObject();

            if (jsonMeta.has("displayName")) {
                itemMeta.setDisplayName(jsonMeta.get("displayName").getAsString());
            }

            if (jsonMeta.has("lore")) {
                JsonArray jsonLore = jsonMeta.getAsJsonArray("lore");
                List<String> lore = new ArrayList<>(jsonLore.size());
                jsonLore.forEach(elemLore -> {
                    if (elemLore.isJsonPrimitive()) {
                        lore.add(elemLore.getAsString());
                    }
                });
                itemMeta.setLore(lore);
            }

            if (jsonMeta.has("enchants")) {
                JsonArray jsonEnchants = jsonMeta.getAsJsonArray("enchants");
                jsonEnchants.forEach(elemEnchant -> ItemMetaJson.addJsonEnchantmentToItemMeta(itemMeta, elemEnchant));
            }

            if (jsonMeta.has("flags")) {
                JsonArray jsonFlags = jsonMeta.getAsJsonArray("flags");
                jsonFlags.forEach(jsonFlag -> {
                    if (jsonFlag.isJsonPrimitive()) {
                        try {
                            ItemFlag flag = ItemFlag.valueOf(jsonFlag.getAsString());
                            itemMeta.addItemFlags(flag);
                        } catch (final IllegalArgumentException ignored) {
                        }
                    }
                });
            }

            if (jsonMeta.has("unbreakable")) {
                itemMeta.setUnbreakable(jsonMeta.get("unbreakable").getAsBoolean());
            }

            if (jsonMeta.has("customModelData")) {
                itemMeta.setCustomModelData(jsonMeta.get("customModelData").getAsInt());
            }

            if (jsonMeta.has("extra")) {
                ItemMetaJson.deserializeExtraMeta(itemMeta, jsonMeta.get("extra"));
            }

            return itemMeta;
        } else {
            return null;
        }
    }

    private static void addJsonEnchantmentToItemMeta(final @NotNull ItemMeta itemMeta, final @NotNull JsonElement elemEnchant) {
        if (elemEnchant.isJsonObject()) {
            JsonObject jsonEnchant = elemEnchant.getAsJsonObject();
            if (jsonEnchant.has("key")) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(jsonEnchant.get("key").getAsString()));
                int level = jsonEnchant.has("level") ? jsonEnchant.get("level").getAsInt() : 1;
                if (enchantment != null && level > 0) {
                    itemMeta.addEnchant(enchantment, level, true);
                }
            }
        }
    }

    @SuppressWarnings ({ "OverlyComplexMethod", "OverlyLongMethod" })
    private static void deserializeExtraMeta(final @NotNull ItemMeta itemMeta, final @NotNull JsonElement elemExtraMeta) {
        if (elemExtraMeta.isJsonObject()) {
            JsonObject jsonExtraMeta = elemExtraMeta.getAsJsonObject();

            if (jsonExtraMeta.has("axolotlBucket")) {
                AxolotlBucketMetaJson.deserialize((AxolotlBucketMeta) itemMeta, jsonExtraMeta.get("axolotlBucket"));
            }

            if (jsonExtraMeta.has("banner")) {
                BannerMetaJson.deserialize((BannerMeta) itemMeta, jsonExtraMeta.get("banner"));
            }

            if (jsonExtraMeta.has("book")) {
                BookMetaJson.deserialize((BookMeta) itemMeta, jsonExtraMeta.get("book"));
            }

            if (jsonExtraMeta.has("bundle")) {
                BundleMetaJson.deserialize((BundleMeta) itemMeta, jsonExtraMeta.get("bundle"));
            }

            if (jsonExtraMeta.has("compass")) {
                CompassMetaJson.deserialize((CompassMeta) itemMeta, jsonExtraMeta.get("compass"));
            }

            if (jsonExtraMeta.has("crossbow")) {
                CrossbowMetaJson.deserialize((CrossbowMeta) itemMeta, jsonExtraMeta.get("crossbow"));
            }

            if (jsonExtraMeta.has("damageable")) {
                DamageableJson.deserialize((Damageable) itemMeta, jsonExtraMeta.get("damageable"));
            }

            if (jsonExtraMeta.has("enchantmentStorage")) {
                EnchantmentStorageMetaJson.deserialize((EnchantmentStorageMeta) itemMeta, jsonExtraMeta.get("enchantmentStorage"));
            }

            if (jsonExtraMeta.has("fireworkEffect")) {
                FireworkEffectMetaJson.deserialize((FireworkEffectMeta) itemMeta, jsonExtraMeta.get("fireworkEffect"));
            }

            if (jsonExtraMeta.has("firework")) {
                FireworkMetaJson.deserialize((FireworkMeta) itemMeta, jsonExtraMeta.get("firework"));
            }

            if (jsonExtraMeta.has("knowledgeBook")) {
                KnowledgeBookMetaJson.deserialize((KnowledgeBookMeta) itemMeta, jsonExtraMeta.get("knowledgeBook"));
            }

            if (jsonExtraMeta.has("leatherArmor")) {
                LeatherArmorMetaJson.deserialize((LeatherArmorMeta) itemMeta, jsonExtraMeta.get("leatherArmor"));
            }

            if (jsonExtraMeta.has("map")) {
                MapMetaJson.deserialize((MapMeta) itemMeta, jsonExtraMeta.get("map"));
            }

            if (jsonExtraMeta.has("potion")) {
                PotionMetaJson.deserialize((PotionMeta) itemMeta, jsonExtraMeta.get("potion"));
            }

            if (jsonExtraMeta.has("skull")) {
                SkullMetaJson.deserialize((SkullMeta) itemMeta, jsonExtraMeta.get("skull"));
            }

            if (jsonExtraMeta.has("suspiciousStew")) {
                SuspiciousStewMetaJson.deserialize((SuspiciousStewMeta) itemMeta, jsonExtraMeta.get("suspiciousStew"));
            }

            if (jsonExtraMeta.has("tropicalFishBucket")) {
                TropicalFishBucketMetaJson.deserialize((TropicalFishBucketMeta) itemMeta, jsonExtraMeta.get("tropicalFishBucket"));
            }
        }
    }

    @SuppressWarnings ({ "MethodWithMoreThanThreeNegations", "OverlyComplexMethod", "OverlyLongMethod" })
    private static @NotNull JsonObject serializeExtraMeta(final @NotNull ItemMeta itemMeta) {
        JsonObject extraMeta = new JsonObject();

        if (itemMeta instanceof AxolotlBucketMeta axolotlBucketMeta) {
            JsonObject jsonMeta = AxolotlBucketMetaJson.serialize(axolotlBucketMeta);
            if (jsonMeta != null) {
                extraMeta.add("axolotlBucket", jsonMeta);
            }
        }

        if (itemMeta instanceof BannerMeta bannerMeta) {
            JsonObject jsonMeta = BannerMetaJson.serialize(bannerMeta);
            if (jsonMeta != null) {
                extraMeta.add("banner", jsonMeta);
            }
        }

        if (itemMeta instanceof BookMeta bookMeta) {
            JsonObject jsonMeta = BookMetaJson.serialize(bookMeta);
            if (jsonMeta != null) {
                extraMeta.add("book", jsonMeta);
            }
        }

        if (itemMeta instanceof BundleMeta bundleMeta) {
            JsonObject jsonMeta = BundleMetaJson.serialize(bundleMeta);
            if (jsonMeta != null) {
                extraMeta.add("bundle", jsonMeta);
            }
        }

        if (itemMeta instanceof CompassMeta compassMeta) {
            JsonObject jsonMeta = CompassMetaJson.serialize(compassMeta);
            if (jsonMeta != null) {
                extraMeta.add("compass", jsonMeta);
            }
        }

        if (itemMeta instanceof CrossbowMeta crossbowMeta) {
            JsonObject jsonMeta = CrossbowMetaJson.serialize(crossbowMeta);
            if (jsonMeta != null) {
                extraMeta.add("crossbow", jsonMeta);
            }
        }

        if (itemMeta instanceof Damageable damageable) {
            JsonObject jsonMeta = DamageableJson.serialize(damageable);
            if (jsonMeta != null) {
                extraMeta.add("damageable", jsonMeta);
            }
        }

        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            JsonObject jsonMeta = EnchantmentStorageMetaJson.serialize(enchantmentStorageMeta);
            if (jsonMeta != null) {
                extraMeta.add("enchantmentStorage", jsonMeta);
            }
        }

        if (itemMeta instanceof FireworkEffectMeta fireworkEffectMeta) {
            JsonObject jsonMeta = FireworkEffectMetaJson.serialize(fireworkEffectMeta);
            if (jsonMeta != null) {
                extraMeta.add("fireworkEffect", jsonMeta);
            }
        }

        if (itemMeta instanceof FireworkMeta fireworkMeta) {
            JsonObject jsonMeta = FireworkMetaJson.serialize(fireworkMeta);
            if (jsonMeta != null) {
                extraMeta.add("firework", jsonMeta);
            }
        }

        if (itemMeta instanceof KnowledgeBookMeta knowledgeBookMeta) {
            JsonObject jsonMeta = KnowledgeBookMetaJson.serialize(knowledgeBookMeta);
            if (jsonMeta != null) {
                extraMeta.add("knowledgeBook", jsonMeta);
            }
        }

        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            JsonObject jsonMeta = LeatherArmorMetaJson.serialize(leatherArmorMeta);
            if (jsonMeta != null) {
                extraMeta.add("leatherArmor", jsonMeta);
            }
        }

        if (itemMeta instanceof MapMeta mapMeta) {
            JsonObject jsonMeta = MapMetaJson.serialize(mapMeta);
            if (jsonMeta != null) {
                extraMeta.add("map", jsonMeta);
            }
        }

        if (itemMeta instanceof PotionMeta potionMeta) {
            JsonObject jsonMeta = PotionMetaJson.serialize(potionMeta);
            if (jsonMeta != null) {
                extraMeta.add("potion", jsonMeta);
            }
        }

        if (itemMeta instanceof SkullMeta skullMeta) {
            JsonObject jsonMeta = SkullMetaJson.serialize(skullMeta);
            if (jsonMeta != null) {
                extraMeta.add("skull", jsonMeta);
            }
        }

        if (itemMeta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            JsonObject jsonMeta = SuspiciousStewMetaJson.serialize(suspiciousStewMeta);
            if (jsonMeta != null) {
                extraMeta.add("suspiciousStew", jsonMeta);
            }
        }

        if (itemMeta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            JsonObject jsonMeta = TropicalFishBucketMetaJson.serialize(tropicalFishBucketMeta);
            if (jsonMeta != null) {
                extraMeta.add("tropicalFishBucketMeta", jsonMeta);
            }
        }

        return extraMeta;
    }
}

package net.erskrayy.eritemclear.item;

import net.erskrayy.eritemclear.config.Settings;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class Filter {

    private final Settings settings;

    public Filter(Settings settings) {
        this.settings = settings;
    }

    public boolean isExcluded(ItemStack stack, String materialName) {
        if (hasExcludedNbt(stack)) return true;

        if (!settings.isExclusionsEnabled()) return false;

        boolean isInList = settings.getExcludedMaterials().contains(materialName);
        return settings.isWhitelistMode() != isInList;
    }

    private boolean hasExcludedNbt(ItemStack stack) {
        if (!settings.isNbtExclusionsEnabled() || settings.getNbtExclusionKeys().isEmpty() || stack == null) return false;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (NamespacedKey key : settings.getNbtExclusionKeys()) {
            if (container.has(key)) {
                return true;
            }
        }
        return false;
    }
}

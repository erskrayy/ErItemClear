package net.erskrayy.eritemclear.config;

import net.erskrayy.eritemclear.BetterItemClear;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Settings {

    private final BetterItemClear plugin;

    private int defaultTime;
    private String hologramFormat;
    private boolean clearCustomEnabled;
    private final Map<String, Integer> customTimes = new HashMap<>();

    private String prefix;
    private String msgNoPerm;
    private String msgReload;
    private String msgClearSuccess;
    private String msgUsage;
    private String msgInfoHeader;
    private String msgInfoTotal;
    private String msgInfoMaterial;
    private String msgInfoFooter;

    private boolean worldTimesEnabled;
    private final Map<String, Integer> worldTimes = new HashMap<>();

    private boolean exclusionsEnabled;
    private boolean whitelistMode;
    private final Set<String> excludedMaterials = new HashSet<>();

    private boolean nbtExclusionsEnabled;
    private final List<NamespacedKey> nbtExclusionKeys = new ArrayList<>();

    private boolean effectsEnabled;
    private String effectType;
    private String particle;
    private String sound;
    private float volume;
    private float pitch;

    public Settings(BetterItemClear plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();

        ConfigurationSection cfg = plugin.getConfig();

        defaultTime = cfg.getInt("default-time", 60);
        hologramFormat = cfg.getString("hologram-format", "&#FB9C08🔥 &n{time}&r сек &7(x{count})");

        clearCustomEnabled = cfg.getBoolean("clear-custom.enabled", true);
        customTimes.clear();
        ConfigurationSection customSection = cfg.getConfigurationSection("clear-custom.materials");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                customTimes.put(key.toUpperCase(), customSection.getInt(key));
            }
        }

        prefix = cfg.getString("messages.prefix", "&8[&bErItemClear&8] ");
        msgNoPerm = cfg.getString("messages.no-permission", "&cУ вас нет прав!");
        msgReload = cfg.getString("messages.reload-success", "&aКонфигурация успешно перезагружена!");
        msgClearSuccess = cfg.getString("messages.clear-success", "&aОчищено предметов: &f{count}");
        msgUsage = cfg.getString("messages.usage", "&fИспользование: &a/eritemclear reload &7| &a/eritemclear clear &7| &a/eritemclear info");
        msgInfoHeader = cfg.getString("messages.info-header", "&8=== &bErItemClear Info &8===");
        msgInfoTotal = cfg.getString("messages.info-total", "&fВсего в трекинге: &a{total} &fпредметов");
        msgInfoMaterial = cfg.getString("messages.info-material", "&f{material}&8: &a{count}");
        msgInfoFooter = cfg.getString("messages.info-footer", "&8====================");

        worldTimesEnabled = cfg.getBoolean("world-times.enabled", false);
        worldTimes.clear();
        ConfigurationSection wtSection = cfg.getConfigurationSection("world-times.worlds");
        if (wtSection != null) {
            for (String world : wtSection.getKeys(false)) {
                worldTimes.put(world, wtSection.getInt(world));
            }
        }

        exclusionsEnabled = cfg.getBoolean("exclusions.enabled", false);
        whitelistMode = cfg.getBoolean("exclusions.whitelist-mode", false);
        excludedMaterials.clear();
        excludedMaterials.addAll(cfg.getStringList("exclusions.materials"));

        nbtExclusionsEnabled = cfg.getBoolean("nbt-exclusions.enabled", false);
        nbtExclusionKeys.clear();
        for (String raw : cfg.getStringList("nbt-exclusions.keys")) {
            NamespacedKey key = parseNamespacedKey(raw);
            if (key != null) {
                nbtExclusionKeys.add(key);
            } else {
                plugin.getLogger().warning("nbt-exclusions: некорректный ключ '" + raw + "', ожидается формат namespace:key — пропущен");
            }
        }

        effectsEnabled = cfg.getBoolean("effects.enabled", false);
        effectType = cfg.getString("effects.effect-type", "PARTICLE").toUpperCase();
        particle = cfg.getString("effects.particle", "CLOUD").toUpperCase();
        sound = cfg.getString("effects.sound", "ENTITY_ITEM_PICKUP").toUpperCase();
        volume = (float) cfg.getDouble("effects.volume", 1.0);
        pitch = (float) cfg.getDouble("effects.pitch", 1.0);
    }

    private NamespacedKey parseNamespacedKey(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return NamespacedKey.fromString(raw.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    public int getDefaultTime() { return defaultTime; }
    public String getHologramFormat() { return hologramFormat; }
    public boolean isClearCustomEnabled() { return clearCustomEnabled; }
    public Map<String, Integer> getCustomTimes() { return customTimes; }

    public String getPrefix() { return prefix; }
    public String getMsgNoPerm() { return msgNoPerm; }
    public String getMsgReload() { return msgReload; }
    public String getMsgClearSuccess() { return msgClearSuccess; }
    public String getMsgUsage() { return msgUsage; }
    public String getMsgInfoHeader() { return msgInfoHeader; }
    public String getMsgInfoTotal() { return msgInfoTotal; }
    public String getMsgInfoMaterial() { return msgInfoMaterial; }
    public String getMsgInfoFooter() { return msgInfoFooter; }

    public boolean isWorldTimesEnabled() { return worldTimesEnabled; }
    public Map<String, Integer> getWorldTimes() { return worldTimes; }

    public boolean isExclusionsEnabled() { return exclusionsEnabled; }
    public boolean isWhitelistMode() { return whitelistMode; }
    public Set<String> getExcludedMaterials() { return excludedMaterials; }

    public boolean isNbtExclusionsEnabled() { return nbtExclusionsEnabled; }
    public List<NamespacedKey> getNbtExclusionKeys() { return nbtExclusionKeys; }

    public boolean isEffectsEnabled() { return effectsEnabled; }
    public String getEffectType() { return effectType; }
    public String getParticle() { return particle; }
    public String getSound() { return sound; }
    public float getVolume() { return volume; }
    public float getPitch() { return pitch; }

    public Integer getCustomTime(String materialName) {
        if (!clearCustomEnabled) return null;
        return customTimes.get(materialName.toUpperCase());
    }
}

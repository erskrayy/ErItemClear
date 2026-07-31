package net.erskrayy.eritemclear.item;

import net.erskrayy.eritemclear.config.Settings;

public class Lifetime {

    private final Settings settings;

    public Lifetime(Settings settings) {
        this.settings = settings;
    }

    public int calculate(String materialName, String worldName) {
        Integer custom = settings.getCustomTime(materialName);
        if (custom != null) return custom;

        if (settings.isWorldTimesEnabled()) {
            Integer worldTime = settings.getWorldTimes().get(worldName);
            if (worldTime != null) return worldTime;
        }

        return settings.getDefaultTime();
    }
}

package net.erskrayy.eritemclear.effect;

import net.erskrayy.eritemclear.config.Settings;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

public class Effects {

    private final JavaPlugin plugin;
    private final Settings settings;

    public Effects(JavaPlugin plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void play(Item item) {
        if (!settings.isEffectsEnabled()) return;

        String type = settings.getEffectType();
        Location loc = item.getLocation();
        boolean wantsParticle = type.equals("PARTICLE") || type.equals("BOTH");
        boolean wantsSound = type.equals("SOUND") || type.equals("BOTH");

        if (wantsParticle) {
            try {
                Particle p = Particle.valueOf(settings.getParticle());
                item.getWorld().spawnParticle(p, loc, 10);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("effects.particle: неизвестная частица '" + settings.getParticle() + "'");
            }
        }

        if (wantsSound) {
            try {
                Sound s = Sound.valueOf(settings.getSound());
                item.getWorld().playSound(loc, s, settings.getVolume(), settings.getPitch());
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("effects.sound: неизвестный звук '" + settings.getSound() + "'");
            }
        }
    }
}

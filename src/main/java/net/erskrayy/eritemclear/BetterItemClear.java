package net.erskrayy.eritemclear;

import net.erskrayy.eritemclear.command.Commands;
import net.erskrayy.eritemclear.config.Settings;
import net.erskrayy.eritemclear.data.TrackedItem;
import net.erskrayy.eritemclear.effect.Effects;
import net.erskrayy.eritemclear.item.Filter;
import net.erskrayy.eritemclear.item.Hologram;
import net.erskrayy.eritemclear.item.Lifetime;
import net.erskrayy.eritemclear.listener.ItemEvents;
import net.erskrayy.eritemclear.service.Banner;
import net.erskrayy.eritemclear.service.UpdateAlert;
import net.erskrayy.eritemclear.task.Cleaner;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BetterItemClear extends JavaPlugin implements Listener {

    private final Map<UUID, TrackedItem> activeHolograms = new HashMap<>();

    private Settings settings;
    private Banner banner;
    private UpdateAlert updateAlert;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        settings = new Settings(this);
        settings.load();

        banner = new Banner(this);
        updateAlert = new UpdateAlert(this);

        Filter filter = new Filter(settings);
        Lifetime lifetime = new Lifetime(settings);
        Hologram hologram = new Hologram(settings);
        Effects effects = new Effects(this, settings);

        getServer().getPluginManager().registerEvents(
                new ItemEvents(this, filter, lifetime, hologram), this);
        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand command = getCommand("eritemclear");
        if (command != null) {
            command.setExecutor(new Commands(this, hologram));
        }

        new Cleaner(this, hologram, effects).runTaskTimer(this, 20L, 20L);

        banner.print("ЗАПУЩЕН");
        updateAlert.check();
    }

    @Override
    public void onDisable() {
        for (TrackedItem data : activeHolograms.values()) {
            if (data == null) continue;

            if (data.getDisplay() != null && !data.getDisplay().isDead()) {
                data.getDisplay().remove();
            }

            Item itemEntity = data.getItem();
            if (itemEntity != null && !itemEntity.isDead()) {
                itemEntity.remove();
            }
        }
        activeHolograms.clear();

        banner.print("ВЫКЛЮЧЕН");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateAlert.notifyIfAvailable(event.getPlayer());
    }

    public void loadConfiguration() {
        settings.load();
    }

    public Map<UUID, TrackedItem> getActiveHolograms() {
        return activeHolograms;
    }

    public Settings getSettings() {
        return settings;
    }
}

package net.erskrayy.eritemclear.task;

import net.erskrayy.eritemclear.BetterItemClear;
import net.erskrayy.eritemclear.data.TrackedItem;
import net.erskrayy.eritemclear.effect.Effects;
import net.erskrayy.eritemclear.item.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Cleaner extends BukkitRunnable {

    private final BetterItemClear plugin;
    private final Hologram hologram;
    private final Effects effects;

    public Cleaner(BetterItemClear plugin, Hologram hologram, Effects effects) {
        this.plugin = plugin;
        this.hologram = hologram;
        this.effects = effects;
    }

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, TrackedItem>> iterator = plugin.getActiveHolograms().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, TrackedItem> entry = iterator.next();
            UUID itemId = entry.getKey();
            TrackedItem data = entry.getValue();

            try {
                processItem(data, iterator);
            } catch (Exception ex) {
                plugin.getLogger().warning("[Cleaner] Ошибка при обработке предмета " + itemId + ": " + ex);
                hologram.remove(data.getDisplay());
                iterator.remove();
            }
        }
    }

    private void processItem(TrackedItem data, Iterator<Map.Entry<UUID, TrackedItem>> iterator) {
        TextDisplay display = data.getDisplay();
        Item item = data.getItem();

        if (item == null || item.isDead() || display == null || display.isDead()) {
            hologram.remove(display);
            iterator.remove();
            return;
        }

        Location loc = item.getLocation();
        boolean isChunkLoaded = loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);

        data.decrementTime();

        if (data.getTimeLeft() <= 0) {
            if (isChunkLoaded) {
                effects.play(item);
            }
            display.remove();
            item.remove();
            iterator.remove();
        } else if (isChunkLoaded) {
            int amount = item.getItemStack().getAmount();
            hologram.updateText(display, data.getTimeLeft(), amount);
        }
    }
}

package net.erskrayy.eritemclear.listener;

import net.erskrayy.eritemclear.BetterItemClear;
import net.erskrayy.eritemclear.data.TrackedItem;
import net.erskrayy.eritemclear.item.Filter;
import net.erskrayy.eritemclear.item.Hologram;
import net.erskrayy.eritemclear.item.Lifetime;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemEvents implements Listener {

    private final BetterItemClear plugin;
    private final Filter filter;
    private final Lifetime lifetime;
    private final Hologram hologram;

    public ItemEvents(BetterItemClear plugin, Filter filter, Lifetime lifetime, Hologram hologram) {
        this.plugin = plugin;
        this.filter = filter;
        this.lifetime = lifetime;
        this.hologram = hologram;
    }

    public void createHologram(Item item) {
        if (item.isDead() || !item.isValid() || plugin.getActiveHolograms().containsKey(item.getUniqueId())) return;

        ItemStack stack = item.getItemStack();
        String materialName = stack.getType().name();
        int amount = stack.getAmount();

        if (filter.isExcluded(stack, materialName)) return;

        int time = lifetime.calculate(materialName, item.getWorld().getName());
        TextDisplay display = hologram.create(item, time, amount);

        item.addPassenger(display);
        Chunk chunk = item.getLocation().getChunk();
        plugin.getActiveHolograms().put(item.getUniqueId(), new TrackedItem(display, item, time, chunk));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        Bukkit.getScheduler().runTask(plugin, () -> createHologram(item));
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        removeHologram(event.getItem());
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        removeHologram(event.getEntity());
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        Item target = event.getTarget();
        removeHologram(event.getEntity());
        updateHologramCountNextTick(target);
    }

    private void updateHologramCountNextTick(Item target) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            TrackedItem data = plugin.getActiveHolograms().get(target.getUniqueId());
            if (data == null || data.getDisplay() == null || data.getDisplay().isDead() || target.isDead()) return;

            int amount = target.getItemStack().getAmount();
            hologram.updateText(data.getDisplay(), data.getTimeLeft(), amount);
        });
    }

    void removeHologram(Item item) {
        TrackedItem data = plugin.getActiveHolograms().remove(item.getUniqueId());
        if (data != null) {
            hologram.remove(data.getDisplay());
        }
    }
}

package net.erskrayy.eritemclear.command;

import net.erskrayy.eritemclear.BetterItemClear;
import net.erskrayy.eritemclear.data.TrackedItem;
import net.erskrayy.eritemclear.item.Hologram;
import net.erskrayy.eritemclear.service.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Commands implements CommandExecutor {

    private final BetterItemClear plugin;
    private final Hologram hologram;

    public Commands(BetterItemClear plugin, Hologram hologram) {
        this.plugin = plugin;
        this.hologram = hologram;
    }

    private void send(CommandSender sender, String text) {
        sender.sendMessage(Color.parse(text));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("eritemclear.admin")) {
            send(sender, plugin.getSettings().getPrefix() + plugin.getSettings().getMsgNoPerm());
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("info")) {
            handleInfo(sender);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            handleClear(sender);
            return true;
        }

        send(sender, plugin.getSettings().getPrefix() + plugin.getSettings().getMsgUsage());
        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.loadConfiguration();
        send(sender, plugin.getSettings().getPrefix() + plugin.getSettings().getMsgReload());
    }

    private void handleInfo(CommandSender sender) {
        send(sender, plugin.getSettings().getMsgInfoHeader());
        send(sender, plugin.getSettings().getMsgInfoTotal()
                .replace("{total}", String.valueOf(plugin.getActiveHolograms().size())));

        Map<String, Integer> materialCounts = new HashMap<>();
        for (TrackedItem data : plugin.getActiveHolograms().values()) {
            Item item = data.getItem();
            if (item != null) {
                String material = item.getItemStack().getType().name();
                materialCounts.merge(material, 1, Integer::sum);
            }
        }

        for (Map.Entry<String, Integer> entry : materialCounts.entrySet()) {
            String msg = plugin.getSettings().getMsgInfoMaterial()
                    .replace("{material}", entry.getKey())
                    .replace("{count}", String.valueOf(entry.getValue()));
            send(sender, msg);
        }

        send(sender, plugin.getSettings().getMsgInfoFooter());
    }

    private void handleClear(CommandSender sender) {
        int removed = 0;

        for (Map.Entry<UUID, TrackedItem> entry : plugin.getActiveHolograms().entrySet()) {
            TrackedItem data = entry.getValue();
            if (data == null) continue;

            hologram.remove(data.getDisplay());

            Item itemEntity = data.getItem();
            if (itemEntity != null && !itemEntity.isDead()) {
                itemEntity.remove();
            }

            removed++;
        }

        plugin.getActiveHolograms().clear();

        String msg = plugin.getSettings().getMsgClearSuccess().replace("{count}", String.valueOf(removed));
        send(sender, plugin.getSettings().getPrefix() + msg);
    }
}

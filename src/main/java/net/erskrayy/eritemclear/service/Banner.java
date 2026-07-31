package net.erskrayy.eritemclear.service;

import net.erskrayy.eritemclear.BetterItemClear;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Banner {

    private final BetterItemClear plugin;

    public Banner(BetterItemClear plugin) {
        this.plugin = plugin;
    }

    public void print(String action) {
        String version = plugin.getPluginMeta().getVersion();

        String[] lines = {
                "<green>==============================",
                "<green>| <aqua>ErItemClear <green>" + action + " <white>Версия: " + version,
                "<green>==============================",
        };

        MiniMessage mm = MiniMessage.miniMessage();
        for (String line : lines) {
            plugin.getServer().getConsoleSender().sendMessage(mm.deserialize(line));
        }
    }
}

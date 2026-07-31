package net.erskrayy.eritemclear.service;

import net.erskrayy.eritemclear.BetterItemClear;
import net.erskrayy.eritemclear.update.GithubApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class UpdateAlert {

    private final BetterItemClear plugin;
    private final GithubApi githubApi;
    private final String githubRepo = "erskrayy/ErItemClear";

    private String availableUpdate = null;

    public UpdateAlert(BetterItemClear plugin) {
        this.plugin = plugin;
        this.githubApi = new GithubApi(plugin, githubRepo);
    }

    public void check() {
        MiniMessage mm = MiniMessage.miniMessage();

        githubApi.getLatestVersion(latestVersion -> {
            if (latestVersion.equals("error")) {
                plugin.getLogger().warning("Не удалось проверить обновления. Возможно, нет интернета или GitHub не пропускает трафик.");
                return;
            }

            String currentVersion = plugin.getPluginMeta().getVersion();

            if (!currentVersion.equalsIgnoreCase(latestVersion) && !currentVersion.contains("dev")) {
                this.availableUpdate = latestVersion;
                String[] consoleMessage = {
                        "<yellow>==============================",
                        "<yellow>| <red>Доступна новая версия ErItemClear: <green>" + latestVersion,
                        "<yellow>| <gray>Твоя версия: <red>" + currentVersion,
                        "<yellow>| <gray>Скачать: <aqua>https://github.com/" + githubRepo + "/releases",
                        "<yellow>=============================="
                };
                for (String line : consoleMessage) {
                    plugin.getServer().getConsoleSender().sendMessage(mm.deserialize(line));
                }
            } else {
                plugin.getServer().getConsoleSender().sendMessage(mm.deserialize(
                        "<gray>[ErItemClear] <green>Используется актуальная версия (" + currentVersion + ")"
                ));
            }
        });
    }

    public void notifyIfAvailable(Player player) {
        if (availableUpdate == null || !player.hasPermission("eritemclear.admin")) return;

        String currentVersion = plugin.getPluginMeta().getVersion();

        Component message = MiniMessage.miniMessage().deserialize(
                "<yellow>==============================\n" +
                        "<yellow>| <red>Доступна новая версия ErItemClear: <green>" + availableUpdate + "\n" +
                        "<yellow>| <gray>Твоя версия: <red>" + currentVersion + "\n" +
                        "<yellow>| <gray>Скачать: <click:open_url:'https://github.com/" + githubRepo + "/releases'><aqua><u>Нажми сюда</u></click>\n" +
                        "<yellow>=============================="
        );

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(message);
            }
        }, 40L);
    }
}

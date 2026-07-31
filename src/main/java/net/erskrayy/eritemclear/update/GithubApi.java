package net.erskrayy.eritemclear.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;

public class GithubApi {

    private final JavaPlugin plugin;
    private final String repo;

    public GithubApi(JavaPlugin plugin, String repo) {
        this.plugin = plugin;
        this.repo = repo;
    }

    public void getLatestVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = URI.create("https://api.github.com/repos/" + this.repo + "/releases").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "ErItemClear-UpdateChecker");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                    reader.close();

                    if (jsonArray.isEmpty()) {
                        consumer.accept("error");
                        return;
                    }

                    JsonObject latestRelease = jsonArray.get(0).getAsJsonObject();
                    String latestVersion = latestRelease.get("tag_name").getAsString();
                    consumer.accept(latestVersion);

                } else {
                    plugin.getLogger().warning("GitHub API отклонил запрос. HTTP Код: " + connection.getResponseCode());
                    consumer.accept("error");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Не удалось проверить обновления: " + e.getMessage());
                consumer.accept("error");
            }
        });
    }
}

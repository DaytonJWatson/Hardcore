package com.daytonjwatson.hardcore.managers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsManager {

    private static StatsManager instance;

    private final JavaPlugin plugin;
    private final Set<UUID> uniquePlayers = new HashSet<>();
    private int totalDeaths = 0;

    private StatsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public static void init(JavaPlugin plugin) {
        if (instance == null) {
            instance = new StatsManager(plugin);
        }
    }

    public static StatsManager get() {
        return instance;
    }

    private void load() {
        plugin.saveDefaultConfig(); // creates config if not present
        FileConfiguration config = plugin.getConfig();

        List<String> uuidStrings = config.getStringList("unique-players");
        for (String s : uuidStrings) {
            try {
                uniquePlayers.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }

        totalDeaths = config.getInt("total-deaths", 0);
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();

        List<String> uuidStrings = uniquePlayers.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        config.set("unique-players", uuidStrings);
        config.set("total-deaths", totalDeaths);

        plugin.saveConfig();
    }

    // Call on join
    public void handleJoin(Player player) {
        if (uniquePlayers.add(player.getUniqueId())) {
            save(); // new unique player, persist immediately
        }
    }

    // Call on death
    public void incrementDeaths() {
        totalDeaths++;
        save();
    }

    public int getUniquePlayerCount() {
        return uniquePlayers.size();
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }
}

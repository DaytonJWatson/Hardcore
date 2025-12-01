package com.daytonjwatson.hardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.daytonjwatson.hardcore.managers.StatsManager;

public class PlayerDeathListener implements Listener {
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
		StatsManager.get().incrementDeaths();
		
        // Choose a sound — you can change it to anything you like.
        Sound sound = Sound.ENTITY_WITHER_DEATH;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(
                    p.getLocation(), // location each player hears it from
                    sound,           // the sound
                    1.0f,            // volume
                    1.0f             // pitch
            );
        }
    }
}

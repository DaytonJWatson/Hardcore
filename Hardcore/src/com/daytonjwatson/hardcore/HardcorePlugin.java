package com.daytonjwatson.hardcore;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daytonjwatson.hardcore.commands.HelpCommand;
import com.daytonjwatson.hardcore.commands.RulesCommand;
import com.daytonjwatson.hardcore.config.Config;
import com.daytonjwatson.hardcore.listeners.PlayerChatListener;
import com.daytonjwatson.hardcore.listeners.PlayerDeathListener;
import com.daytonjwatson.hardcore.listeners.PlayerJoinListener;
import com.daytonjwatson.hardcore.listeners.PlayerQuitListener;
import com.daytonjwatson.hardcore.managers.StatsManager;

public class HardcorePlugin extends JavaPlugin {
	
	public static HardcorePlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		StatsManager.init(this);
		
		Config.setup();
		
		loadCommands();
		loadListeners();
	}
	
	@Override
	public void onDisable() {
		if (StatsManager.get() != null) {
	        StatsManager.get().save();
	    }
	}
	
	private void loadCommands() {
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("rules").setExecutor(new RulesCommand());
	}
	
	private void loadListeners() {
		PluginManager pm = instance.getServer().getPluginManager();
		
		pm.registerEvents(new PlayerChatListener(), instance);
		pm.registerEvents(new PlayerDeathListener(), instance);
		pm.registerEvents(new PlayerJoinListener(), instance);
		pm.registerEvents(new PlayerQuitListener(), instance);
	}

}

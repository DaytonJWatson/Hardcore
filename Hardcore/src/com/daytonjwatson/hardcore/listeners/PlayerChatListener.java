package com.daytonjwatson.hardcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.daytonjwatson.hardcore.utils.Util;

public class PlayerChatListener implements Listener {
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat(Util.color("&7<" + event.getPlayer().getName() + "> " + event.getMessage()));
	}
}

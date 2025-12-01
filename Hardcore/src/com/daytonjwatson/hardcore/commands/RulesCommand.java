package com.daytonjwatson.hardcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommand implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "========== SERVER RULES ==========");

        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hardcore Environment:");
        sender.sendMessage(ChatColor.GRAY + "• This is a one-life server.");
        sender.sendMessage(ChatColor.GRAY + "• If you die, you are "
                + ChatColor.DARK_RED + "" + ChatColor.BOLD + "PERMANENTLY BANNED" 
                + ChatColor.GRAY + ".");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Gameplay Rules:");
        sender.sendMessage(ChatColor.GRAY + "• PvP & Griefing is allowed but "
                + ChatColor.RED + "strongly frowned upon" 
                + ChatColor.GRAY + ".");
        sender.sendMessage(ChatColor.GRAY + "• Stealing is allowed.");
        sender.sendMessage(ChatColor.GRAY + "• No land claiming or protection.");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Behavior Rules:");
        sender.sendMessage(ChatColor.GRAY + "• No hacking, cheating, or clients that give unfair advantages.");
        sender.sendMessage(ChatColor.GRAY + "• No exploiting glitches or dupes.");
        sender.sendMessage(ChatColor.GRAY + "• No spam or harassment in chat.");
        sender.sendMessage(ChatColor.GRAY + "• Respect other players—even if PvP is allowed.");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Play smart.");
        sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Trust nobody.");
        sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You only get one life.");

        return true;
    }
}

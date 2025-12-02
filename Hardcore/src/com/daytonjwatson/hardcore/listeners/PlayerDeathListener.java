package com.daytonjwatson.hardcore.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.daytonjwatson.hardcore.managers.BanditManager;
import com.daytonjwatson.hardcore.managers.StatsManager;
import com.daytonjwatson.hardcore.utils.GearPowerUtil;
import com.daytonjwatson.hardcore.utils.GearPowerUtil.CombatSnapshot;
import com.daytonjwatson.hardcore.utils.TabUtil;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        StatsManager stats = StatsManager.get();
        stats.handleDeath(victim, killer);

        // No killer = environmental / mob / suicide → still do sound/tab, but no PvP announcements
        if (killer != null) {
            UUID victimId = victim.getUniqueId();
            UUID killerId = killer.getUniqueId();

            // Status BEFORE any updates (important for demotion Hero→Bandit messaging)
            boolean victimWasBandit = stats.isBandit(victimId);
            boolean killerWasBandit = stats.isBandit(killerId);
            boolean killerWasHero   = stats.isHero(killerId);

            // ------------------------------------------------
            // 1) VICTIM IS BANDIT → redemption / hero progress
            // ------------------------------------------------
            if (victimWasBandit) {

                // --- BANDIT REDEMPTION: bandit killing bandit ---
                if (killerWasBandit) {
                    boolean lostBandit = stats.handleBanditKill(killerId);
                    int hunterKills = stats.getBanditHunterKills(killerId);
                    int needed = Math.max(0, 3 - hunterKills);

                    if (lostBandit) {
                        // Private message
                        killer.sendMessage(ChatColor.GREEN +
                                "You have redeemed yourself by slaying 3 bandits. Your bandit status has been removed.");

                        // Broadcast redemption
                        Bukkit.broadcastMessage(
                                ChatColor.DARK_GRAY + "[" +
                                        ChatColor.GREEN + "" + ChatColor.BOLD + "REDEEMED" +
                                        ChatColor.DARK_GRAY + "] " +
                                        ChatColor.GREEN + killer.getName() +
                                        ChatColor.GRAY + " has redeemed themselves by slaying 3 bandits and is no longer a " +
                                        ChatColor.DARK_RED + "Bandit" + ChatColor.GRAY + "."
                        );
                    } else {
                        killer.sendMessage(ChatColor.YELLOW + "Bandit redemption progress: " +
                                ChatColor.GOLD + hunterKills + "/3");
                        killer.sendMessage(ChatColor.GRAY + "You need " +
                                ChatColor.RED + needed +
                                ChatColor.GRAY + " more bandit kills to lose your bandit status.");
                    }
                }

                // --- HERO PROGRESSION: non-bandit killing bandit ---
                else {
                    boolean becameHero = stats.handleHeroBanditKill(killerId);
                    int heroKills = stats.getHeroBanditKills(killerId);
                    int needed = Math.max(0, 3 - heroKills);

                    if (becameHero) {
                        // Private message
                        killer.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD +
                                "You are now recognized as a HERO for slaying 3 bandits!");

                        // Broadcast hero promotion
                        Bukkit.broadcastMessage(
                                ChatColor.DARK_GRAY + "[" +
                                        ChatColor.GOLD + "" + ChatColor.BOLD + "HERO" +
                                        ChatColor.DARK_GRAY + "] " +
                                        ChatColor.GOLD + killer.getName() +
                                        ChatColor.GRAY + " has become a " +
                                        ChatColor.GOLD + "" + ChatColor.BOLD + "HERO" +
                                        ChatColor.GRAY + " by defeating 3 bandits!"
                        );
                    }
                    // Only show hero progress if they are not already a Hero
                    else if (!killerWasHero && !stats.isHero(killerId)) {
                        // (extra isHero check if you later allow other ways to gain Hero)
                        killer.sendMessage(ChatColor.YELLOW + "Hero progress: " +
                                ChatColor.GOLD + heroKills + "/3");
                        killer.sendMessage(ChatColor.GRAY + "You need " +
                                ChatColor.GOLD + needed +
                                ChatColor.GRAY + " more bandit kills to become a Hero.");
                    }
                }

                // Killing bandits is NEVER counted as an unfair kill in BanditManager.
            }

            // ------------------------------------------------
            // 2) VICTIM IS NOT A BANDIT → unfair-kill / bandit logic
            // ------------------------------------------------
            else {
                // This handles unfair-kill bandit count and possibly promotion to bandit.
                // Returns true ONLY when killer just hit 3 unfair kills.
                boolean becameBandit = BanditManager.handlePotentialBanditKill(victim, killer);

                if (becameBandit) {
                    // At this point StatsManager.handleUnfairKill should already
                    // have added bandit status AND stripped hero (if they were hero).

                    boolean killerIsNowBandit = stats.isBandit(killerId);
                    boolean killerIsNowHero   = stats.isHero(killerId); // should be false if demoted

                    // If they were a Hero BEFORE the kill, treat this as a demotion.
                    if (killerWasHero && killerIsNowBandit && !killerIsNowHero) {
                        killer.sendMessage(
                                ChatColor.DARK_RED + "" + ChatColor.BOLD +
                                "You have fallen from HERO to BANDIT " +
                                ChatColor.RED + "for repeatedly preying on weaker players."
                        );

                        Bukkit.broadcastMessage(
                                ChatColor.DARK_GRAY + "[" +
                                        ChatColor.DARK_RED + "" + ChatColor.BOLD + "DEMOTION" +
                                        ChatColor.DARK_GRAY + "] " +
                                        ChatColor.GOLD + killer.getName() +
                                        ChatColor.GRAY + " has fallen from " +
                                        ChatColor.GOLD + "" + ChatColor.BOLD + "HERO " +
                                        ChatColor.GRAY + "to " +
                                        ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDIT " +
                                        ChatColor.GRAY + "after repeatedly attacking weaker players."
                        );
                    } else {
                        // Normal promotion to bandit from regular
                        killer.sendMessage(
                                ChatColor.DARK_RED + "" + ChatColor.BOLD +
                                "You are now a BANDIT. Your violent reputation precedes you..."
                        );

                        Bukkit.broadcastMessage(
                                ChatColor.DARK_GRAY + "[" +
                                        ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDIT" +
                                        ChatColor.DARK_GRAY + "] " +
                                        ChatColor.RED + killer.getName() +
                                        ChatColor.GRAY + " has become a " +
                                        ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDIT" +
                                        ChatColor.GRAY + " after repeatedly preying on weaker players."
                        );
                    }
                }
            }

         // ------------------------------------------------
         // 3) CUSTOM PVP DEATH MESSAGE (REPLACES VANILLA)
         // ------------------------------------------------

         // Remove vanilla death message completely
         event.setDeathMessage(null);

         // Recompute AFTER all logic
         boolean killerIsBanditNow = stats.isBandit(killerId);
         boolean killerIsHeroNow   = stats.isHero(killerId);

         boolean victimIsBanditNow = stats.isBandit(victimId);
         boolean victimIsHeroNow   = stats.isHero(victimId);

         // Build prefixes (same format as your chat prefix)
         String killerPrefix =
        	        killerIsBanditNow ? ChatColor.DARK_RED + "" + ChatColor.BOLD + "[B] " :
        	        killerIsHeroNow   ? ChatColor.GOLD     + "" + ChatColor.BOLD + "[H] " :
        	                            "";

        	String victimPrefix =
        	        victimIsBanditNow ? ChatColor.DARK_RED + "" + ChatColor.BOLD + "[B] " :
        	        victimIsHeroNow   ? ChatColor.GOLD     + "" + ChatColor.BOLD + "[H] " :
        	                            "";

         // Snapshot gear/power
         CombatSnapshot killerSnap = GearPowerUtil.getCombatSnapshot(killer);
         CombatSnapshot victimSnap = GearPowerUtil.getCombatSnapshot(victim);

         int killerGearPower = killerSnap.gearPower;
         int victimGearPower = victimSnap.gearPower;
         double killerTotal  = killerSnap.totalPower;
         double victimTotal  = victimSnap.totalPower;

         // Final custom death message (no role prefix)
         String killMsg =
        	        ChatColor.RED + killerPrefix + killer.getName() + ChatColor.GRAY +
        	        " killed " +
        	        ChatColor.WHITE + victimPrefix + victim.getName() +
        	        ChatColor.GRAY + " (" +
        	        ChatColor.RED + "Gear " + killerGearPower +
        	        ChatColor.GRAY + " vs " +
        	        ChatColor.WHITE + "Gear " + victimGearPower +
        	        ChatColor.GRAY + ", " +
        	        ChatColor.RED + "Power " + (int) Math.round(killerTotal) +
        	        ChatColor.GRAY + " vs " +
        	        ChatColor.WHITE + "Power " + (int) Math.round(victimTotal) +
        	        ChatColor.GRAY + ")";

         // Broadcast the new death message
         Bukkit.broadcastMessage(killMsg);
        }

        // Update tab prefixes / status (Bandit / Hero etc.)
        TabUtil.updateTabForAll();

        // Global death sound
        Sound sound = Sound.ENTITY_WITHER_DEATH;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
        }
    }
}

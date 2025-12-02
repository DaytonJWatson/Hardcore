package com.daytonjwatson.hardcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class GuideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        sendGuideChat(player);
        giveGuideBook(player);

        return true;
    }

    private void sendGuideChat(Player p) {

        p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "========= BANDITS & HEROES =========");

        p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Bandits");
        p.sendMessage(ChatColor.GRAY + "- Killing much weaker players in unfair fights gives Bandit Points.");
        p.sendMessage(ChatColor.GRAY + "- At " + ChatColor.RED + "3 unfair kills" + ChatColor.GRAY + " you become a "
                + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Bandit" + ChatColor.GRAY + ".");
        p.sendMessage(ChatColor.GRAY + "- Bandits are publicly announced and marked with "
                + ChatColor.DARK_RED + "[B]" + ChatColor.GRAY + ".");

        p.sendMessage("");

        p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Redemption");
        p.sendMessage(ChatColor.GRAY + "- Bandits who kill other bandits gain redemption progress.");
        p.sendMessage(ChatColor.GRAY + "- At " + ChatColor.GREEN + "3 bandits slain as a bandit"
                + ChatColor.GRAY + ", you lose your Bandit status and return to normal.");

        p.sendMessage("");

        p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Heroes");
        p.sendMessage(ChatColor.GRAY + "- Non-bandits who kill bandits gain Hero progress.");
        p.sendMessage(ChatColor.GRAY + "- At " + ChatColor.GOLD + "3 bandits slain as a non-bandit"
                + ChatColor.GRAY + ", you become a "
                + ChatColor.GOLD + "" + ChatColor.BOLD + "Hero" + ChatColor.GRAY + ".");
        p.sendMessage(ChatColor.GRAY + "- Heroes are marked with "
                + ChatColor.GOLD + "[H]" + ChatColor.GRAY + " and announced.");
        p.sendMessage(ChatColor.GRAY + "- You can still become a Hero after being a Bandit, but only after you redeem first.");

        p.sendMessage("");

        p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "====================================");
    }

    private void giveGuideBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;

        meta.setTitle("Bandits & Heroes");
        meta.setAuthor(player.getServer().getName());

        // Page 1: Bandits
        String page1 =
                ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDITS\n\n" +
                ChatColor.GRAY +
                "Killing much\n" +
                "weaker players in\n" +
                "unfair fights gives\n" +
                "you " + ChatColor.DARK_RED + "Bandit Points" + ChatColor.GRAY + ".\n\n" +
                "At " + ChatColor.RED + "3 unfair kills\n" +
                ChatColor.GRAY + "you become a\n" +
                ChatColor.DARK_RED + "" + ChatColor.BOLD + "Bandit" + ChatColor.GRAY + ".\n\n" +
                "Bandits are marked\n" +
                "with " + ChatColor.DARK_RED + "[B]" + ChatColor.GRAY + " and are\n" +
                "announced to all.";

        // Page 2: Redemption
        String page2 =
                ChatColor.GREEN + "" + ChatColor.BOLD + "REDEMPTION\n\n" +
                ChatColor.GRAY +
                "Bandits who kill\n" +
                "other bandits gain\n" +
                ChatColor.GREEN + "Redemption Points" + ChatColor.GRAY + ".\n\n" +
                "At " + ChatColor.GREEN + "3 bandits slain\n" +
                ChatColor.GRAY + "while you are a\n" +
                "Bandit, you lose\n" +
                "your " + ChatColor.DARK_RED + "Bandit status\n" +
                ChatColor.GRAY + "and return to\n" +
                "normal.\n\n";

        // Page 3: Heroes
        String page3 =
                ChatColor.GOLD + "" + ChatColor.BOLD + "HEROES\n\n" +
                ChatColor.GRAY +
                "Players who are\n" +
                "not Bandits and\n" +
                "kill Bandits gain\n" +
                ChatColor.GOLD + "Hero Points" + ChatColor.GRAY + ".\n\n" +
                "At " + ChatColor.GOLD + "3 bandits slain\n" +
                ChatColor.GRAY + "while you are not\n" +
                "a Bandit, you\n" +
                "become a " + ChatColor.GOLD + "" + ChatColor.BOLD + "Hero" + ChatColor.GRAY + ".\n\n" +
                "Heroes are marked\n" +
                "with " + ChatColor.GOLD + "[H]";

        // Page 4: Flow summary
        String page4 =
                ChatColor.GOLD + "" + ChatColor.BOLD + "STATUS FLOW\n\n" +
                ChatColor.GRAY +
                "Fair player:\n" +
                "  kill 3 bandits\n" +
                "  " + ChatColor.GOLD + "→ Hero\n\n" +
                ChatColor.GRAY +
                "Fair player:\n" +
                "  3 unfair kills\n" +
                "  " + ChatColor.DARK_RED + "→ Bandit\n\n" +
                ChatColor.GRAY +
                "Bandit:\n" +
                "  kill 3 bandits\n" +
                "  " + ChatColor.GREEN + "→ Redeemed\n\n" +
                ChatColor.GRAY +
                "Redeemed:\n" +
                "  kill 3 bandits\n" +
                "  as non-bandit\n" +
                "  " + ChatColor.GOLD + "→ Hero";

        meta.addPage(page1, page2, page3, page4);
        book.setItemMeta(meta);

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), book);
            player.sendMessage(ChatColor.YELLOW +
                    "Your inventory was full, so the Bandits & Heroes guide was dropped at your feet.");
        } else {
            player.getInventory().addItem(book);
            player.sendMessage(ChatColor.GOLD + "You have received the "
                    + ChatColor.DARK_RED + "Bandits & Heroes"
                    + ChatColor.GOLD + " guide book.");
        }
    }
}

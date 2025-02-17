package de.deeprobin.earny.platform.bukkit.command;

import de.deeprobin.earny.exception.ShorteningException;
import de.deeprobin.earny.platform.bukkit.EarnyPlugin;
import de.deeprobin.earny.shorteners.IShortener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ShortUrlCommand implements CommandExecutor, TabCompleter {

    private final EarnyPlugin plugin;

    public ShortUrlCommand(final EarnyPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getLogger().info("Initialized short-url command");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            String shortenerString = args[0];
            String url = args[1];

            IShortener shortener = this.plugin.getFactory().getShortenerManager().getShortenerByName(shortenerString, false);

            if (shortener == null) {
                sender.sendMessage(ChatColor.RED + String.format("Shortener %s is not available.", shortenerString.toUpperCase()));
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Please wait. Generating shortened link...");

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                try {
                    String shortUrl = shortener.shortUrl(url);
                    ComponentBuilder builder = new ComponentBuilder("Short URL: ");
                    builder.color(ChatColor.GOLD);
                    builder.append(shortUrl);
                    builder.color(ChatColor.YELLOW);
                    builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, shortUrl));
                    builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to open url.")}));
                    sender.spigot().sendMessage(builder.create());
                } catch (ShorteningException e) {
                    sender.sendMessage("§cCannot short url. Please check the api key for this service. Stack Trace: " + this.plugin.getFactory().getErrorReportUtil().getErrorReport(e));
                }
            });


        } else {
            ComponentBuilder builder = new ComponentBuilder("--- EARNY - SHORT-URL ---");
            builder.color(ChatColor.GOLD);
            builder.append("\n");
            builder.append("Syntax: /" + command.getName() + " <shortener> <url>");
            builder.color(ChatColor.RED);
            builder.append("\nAvailable shorteners: ").color(ChatColor.GOLD);
            for (IShortener s : this.plugin.getFactory().getShortenerManager().getShorteners()) {
                builder.append("* ").color(ChatColor.GOLD);
                builder.append(s.getIdentifiers()[0]).color(ChatColor.YELLOW);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length <= 1) {
            for (IShortener s : this.plugin.getFactory().getShortenerManager().getShorteners()) {
                completions.addAll(Arrays.asList(s.getIdentifiers()));
            }
        }
        return completions;
    }
}

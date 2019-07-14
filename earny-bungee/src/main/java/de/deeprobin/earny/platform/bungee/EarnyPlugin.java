package de.deeprobin.earny.platform.bungee;

import de.deeprobin.earny.IPlugin;
import de.deeprobin.earny.PluginFactory;
import de.deeprobin.earny.logging.JavaLoggerImplementation;
import de.deeprobin.earny.platform.bungee.command.ShortUrlCommand;
import de.deeprobin.earny.platform.bungee.listener.ChatListener;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

public class EarnyPlugin extends Plugin implements IPlugin {

    @Getter
    private PluginFactory factory;

    @Override
    public void onEnable() {
        this.factory = new PluginFactory(new JavaLoggerImplementation(this.getLogger()), this);
        this.factory.run();
        Metrics metrics = new Metrics(this);
        this.getLogger().info("Initialised bStats metrics.");

        this.getProxy().getPluginManager().registerListener(this, new ChatListener(this));
        this.getProxy().getPluginManager().registerCommand(this, new ShortUrlCommand(this));

        this.getLogger().info("Done.");
    }

    @Override
    public String getConfigDir() {
        return this.getDataFolder().getAbsolutePath();
    }

    @Override
    public String getServerVersion() {
        return this.getProxy().getVersion();
    }

    @Override
    public String getGameVersion() {
        return "Different by player";
    }
}

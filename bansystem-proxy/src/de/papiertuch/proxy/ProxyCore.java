package de.papiertuch.proxy;

import de.papiertuch.proxy.commands.KickCommand;
import de.papiertuch.proxy.commands.LoginCommand;
import de.papiertuch.proxy.commands.ban.*;
import de.papiertuch.proxy.commands.mute.*;
import de.papiertuch.proxy.listener.ChatListener;
import de.papiertuch.proxy.listener.LoginListener;
import de.papiertuch.proxy.listener.PlayerDisconnectListener;
import de.papiertuch.proxy.listener.PostLoginListener;
import de.papiertuch.proxy.metrics.Metrics;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.ProxiedCommandSender;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

@Getter
public class ProxyCore extends Plugin {

    @Getter
    private static ProxyCore instance;
    private IBanPlayer consolePlayer;


    @Override
    public void onEnable() {
        instance = this;

        new BanSystem(this.getProxy().getVersion(), this.getDescription().getVersion());

        this.consolePlayer =
                BanSystem.getInstance().loadBanPlayer(new ProxiedCommandSender(this.getProxy().getConsole()));

        register();

        new Metrics(this, 11744);
    }

    private void register() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new ChatListener());
        pluginManager.registerListener(this, new LoginListener());
        pluginManager.registerListener(this, new PlayerDisconnectListener());
        pluginManager.registerListener(this, new PostLoginListener());

        pluginManager.registerCommand(this, new BanCommand());
        pluginManager.registerCommand(this, new BanHistoryCommand());
        pluginManager.registerCommand(this, new BanPointsCommand());
        pluginManager.registerCommand(this, new BanReduceCommand());
        pluginManager.registerCommand(this, new TempBanCommand());
        pluginManager.registerCommand(this, new UnbanCommand());

        pluginManager.registerCommand(this, new MuteCommand());
        pluginManager.registerCommand(this, new MuteHistoryCommand());
        pluginManager.registerCommand(this, new MuteReduceCommand());
        pluginManager.registerCommand(this, new TempMuteCommand());
        pluginManager.registerCommand(this, new UnmuteCommand());

        pluginManager.registerCommand(this, new KickCommand());
        pluginManager.registerCommand(this, new LoginCommand());
    }
}

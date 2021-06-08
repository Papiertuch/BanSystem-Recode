package de.papiertuch.proxy;

import de.papiertuch.proxy.commands.KickCommand;
import de.papiertuch.proxy.commands.LoginCommand;
import de.papiertuch.proxy.commands.ban.BanCommand;
import de.papiertuch.proxy.commands.ban.BanPointsCommand;
import de.papiertuch.proxy.commands.ban.TempBanCommand;
import de.papiertuch.proxy.commands.ban.UnbanCommand;
import de.papiertuch.proxy.listener.ChatListener;
import de.papiertuch.proxy.listener.LoginListener;
import de.papiertuch.proxy.listener.PlayerDisconnectListener;
import de.papiertuch.proxy.listener.PostLoginListener;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
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
    private Reason banBypassingReason;

    @Override
    public void onEnable() {
        instance = this;

        new BanSystem(this.getProxy().getVersion(), this.getDescription().getVersion());

        this.consolePlayer = BanSystem.getInstance().loadBanPlayer(new ProxiedCommandSender(getProxy().getConsole()));
        this.banBypassingReason = new Reason(BanSystem.getInstance().getConfig().getString("settings.banBypassing.name"), 0,
                BanSystem.getInstance().getConfig().getString("settings.banBypassing.duration"), 0, false);

        register();
    }

    private void register() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new LoginListener());
        pluginManager.registerListener(this, new PostLoginListener());
        pluginManager.registerListener(this, new ChatListener());
        pluginManager.registerListener(this, new PlayerDisconnectListener());

        pluginManager.registerCommand(this, new BanCommand());
        pluginManager.registerCommand(this, new UnbanCommand());
        pluginManager.registerCommand(this, new KickCommand());
        pluginManager.registerCommand(this, new LoginCommand());
        pluginManager.registerCommand(this, new TempBanCommand());
        pluginManager.registerCommand(this, new BanPointsCommand());
    }
}

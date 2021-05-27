package de.papiertuch.proxy;

import de.papiertuch.proxy.commands.ban.BanCommand;
import de.papiertuch.proxy.listener.LoginListener;
import de.papiertuch.proxy.listener.PostLoginListener;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.ProxiedCommandSender;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ProxyCore extends Plugin {

    @Getter
    private static ProxyCore instance;

    @Override
    public void onEnable() {
        instance = this;

        new BanSystem(this.getProxy().getVersion(), this.getDescription().getVersion());

        BanSystem.getInstance().loadBanPlayer(new ProxiedCommandSender(getProxy().getConsole()));

        register();
    }

    private void register() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new LoginListener());
        pluginManager.registerListener(this, new PostLoginListener());

        pluginManager.registerCommand(this, new BanCommand());
    }
}

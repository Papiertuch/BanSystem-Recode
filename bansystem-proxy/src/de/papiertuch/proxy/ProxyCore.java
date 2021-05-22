package de.papiertuch.proxy;

import de.papiertuch.proxy.listener.LoginListener;
import de.papiertuch.proxy.listener.PostLoginListener;
import de.papiertuch.utils.BanSystem;
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

        register();
    }

    private void register() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new LoginListener());
        pluginManager.registerListener(this, new PostLoginListener());
    }
}

package de.papiertuch.bukkit;

import de.papiertuch.bukkit.commands.KickCommand;
import de.papiertuch.bukkit.commands.LoginCommand;
import de.papiertuch.bukkit.metrics.Metrics;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.BukkitCommandSender;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;
    private IBanPlayer consolePlayer;

    @Override
    public void onEnable() {
        instance = this;

        new BanSystem(this.getServer().getBukkitVersion(), this.getDescription().getVersion());

        this.consolePlayer =
                BanSystem.getInstance().loadBanPlayer(new BukkitCommandSender(this.getServer().getConsoleSender()));

        register();

        new Metrics(this, 11990);
    }

    private void register() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        try {
            Class<?> clazz = this.reflectCraftClazz(".CraftServer");
            CommandMap commandMap = (CommandMap) clazz.getMethod("getCommandMap").invoke(Bukkit.getServer());
            commandMap.register("kick", new KickCommand());
            commandMap.register("login", new LoginCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> reflectCraftClazz(String suffix) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + suffix);
        } catch (Exception var4) {
            try {
                return Class.forName("org.bukkit.craftbukkit." + suffix);
            } catch (ClassNotFoundException var3) {
                return null;
            }
        }
    }

}

package de.papiertuch.utils;

import com.google.gson.JsonParser;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

public class UUIDFetcher {

    private HashMap<String, UUID> cache;

    public UUIDFetcher() {
        this.cache = new HashMap<>();
    }

    public UUID getUUID(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
            if (offlinePlayer != null) {
                cache.put(name, offlinePlayer.getUniqueId());
                return cache.get(name);
            }
        }
        if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
            ICloudOfflinePlayer offlinePlayer = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getFirstOfflinePlayer(name);
            if (offlinePlayer != null) {
                cache.put(name, offlinePlayer.getUniqueId());
                return cache.get(name);
            }
        }
        try {
            URLConnection urlConnection = new URL("https://api.minetools.eu/uuid/" + name).openConnection();
            urlConnection.setReadTimeout(2000);
            String uuidAsString = new JsonParser().parse(new InputStreamReader(urlConnection.getInputStream()))
                    .getAsJsonObject().get("id").toString().replace("\"", "");

            cache.put(name, UUID.fromString(new StringBuffer(uuidAsString)
                    .insert(8, "-")
                    .insert(13, "-")
                    .insert(18, "-")
                    .insert(23, "-")
                    .toString()));
            return cache.get(name);
        } catch (Exception e) {
            System.out.println("[BanSystem] failed fetch uuid of " + name + " this player no exists");
        }
        return UUID.randomUUID();
    }
}

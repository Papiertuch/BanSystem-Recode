package de.papiertuch.utils.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Config {

    private String config;
    private File file;
    private Path path;
    private Configuration configuration;
    private HashMap<String, Object> cache;

    public Config(String config) {
        this.config = config;
        this.cache = new HashMap<>();
        this.file = new File("plugins/BanSystem");
        this.path = Paths.get("plugins/BanSystem/" + config);

        if (!file.exists()) file.mkdirs();
        try (InputStream localInputStream = Config.class.getClassLoader().getResourceAsStream(config)) {
            if (!Files.exists(path)) {
                assert localInputStream != null;
                Files.copy(localInputStream, this.path);
            }
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(path.toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getList(String type) {
        if (this.cache.containsKey(type)) {
            return (List<String>) this.cache.get(type);
        }
        this.cache.put(type, this.configuration.getStringList(type));
        return (List<String>) this.cache.get(type);
    }

    public int getInt(String type) {
        if (this.cache.containsKey(type)) {
            return (int) this.cache.get(type);
        }
        if (configuration.get(type) == null) {
            System.out.println("[Punish] empty value for type (" + type + ")");
            return 0;
        }
        this.cache.put(type, this.configuration.getInt(type));
        return (int) this.cache.get(type);
    }

    public boolean getBoolean(String type) {
        if (this.cache.containsKey(type)) {
            return (boolean) this.cache.get(type);
        }
        if (configuration.get(type) == null) {
            System.out.println("[Punish] empty value for type (" + type + ")");
            return false;
        }
        this.cache.put(type, this.configuration.getBoolean(type));
        return (boolean) this.cache.get(type);
    }

    public String getString(String type) {
        if (this.cache.containsKey(type)) {
            return (String) this.cache.get(type);
        }
        if (configuration.get(type) == null) {
            System.out.println("[Punish] value \"" + type + "\" could not be found in the file \"" + config + "\"");
            return null;
        }
        this.cache.put(type, this.configuration.getString(type)
                .replace("%prefix%", this.configuration.getString("messages.prefix")));
        return (String) this.cache.get(type);
    }

    public String getListAsString(String key) {
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String screen : getList(key)) {
            i++;
            if (i == 1) {
                stringBuilder.append(screen.replace("%prefix%",
                        this.configuration.getString("messages.prefix")));
            } else {
                stringBuilder.append("\n" + screen.replace("%prefix%",
                        this.configuration.getString("messages.prefix")));
            }
        }
        return stringBuilder.toString();
    }
}

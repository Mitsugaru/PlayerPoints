package org.black_ixx.playerpoints.update.modules;

import java.io.File;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.Version;
import org.black_ixx.playerpoints.update.UpdateModule;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the update to 1.5 from previous versions.
 * 
 * @author Mitsugaru
 */
public class OneDotFiveUpdate extends UpdateModule {

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public OneDotFiveUpdate(PlayerPoints plugin) {
        super(plugin);
        targetVersion = new Version("1.5");
        targetVersion.setIgnorePatch(true);
    }

    @Override
    public void update() {
        // rename old config to storage
        final File storage = new File(plugin.getDataFolder() + File.separator
                + "config.yml");
        boolean success = false;
        try {
            success = storage.renameTo(new File(plugin.getDataFolder()
                    + File.separator + "storage.yml"));
        } catch(SecurityException sec) {
            plugin.getLogger().severe(
                    "SecurityExcpetion on renaming config.yml to storage.yml");
            sec.printStackTrace();
        } catch(NullPointerException npe) {
            plugin.getLogger()
                    .severe("NullPointerException on renaming config.yml to storage.yml");
            npe.printStackTrace();
        }
        if(success) {
            // Set new config with defaults
            plugin.reloadConfig();
            final ConfigurationSection config = plugin.getConfig();
            config.set("storage", "YAML");
            config.set("mysql.host", "localhost");
            config.set("mysql.port", 3306);
            config.set("mysql.database", "minecraft");
            config.set("mysql.user", "username");
            config.set("mysql.password", "pass");
            config.set("mysql.import.use", false);
            config.set("mysql.import.source", "YAML");
            config.set("vote.enabled", false);
            config.set("vote.amount", 100);
            config.set("vote.online", false);
            config.set("debug.database", false);
            plugin.saveConfig();
        } else {
            plugin.getLogger().severe(
                    "Failed to rename file config.yml to storage.yml");
        }
    }

}

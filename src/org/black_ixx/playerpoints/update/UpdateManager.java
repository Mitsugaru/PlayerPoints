package org.black_ixx.playerpoints.update;

import java.util.SortedSet;
import java.util.TreeSet;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.version.Version;
import org.black_ixx.playerpoints.update.modules.OneFiveTwoUpdate;
import org.black_ixx.playerpoints.update.modules.OneFiveUpdate;
import org.bukkit.configuration.ConfigurationSection;

public class UpdateManager {

    /**
     * Plugin instance.
     */
    private PlayerPoints plugin;

    /**
     * Update modules.
     */
    private final SortedSet<UpdateModule> modules = new TreeSet<UpdateModule>();

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public UpdateManager(final PlayerPoints plugin) {
        this.plugin = plugin;

        modules.add(new OneFiveUpdate(plugin));
        modules.add(new OneFiveTwoUpdate(plugin));
    }

    /**
     * Check if updates are necessary
     */
    public void checkUpdate() {
        // Check if need to update
        final ConfigurationSection config = plugin.getConfig();
        final Version version = new Version(plugin.getDescription()
                .getVersion());
        if(!version.validate()) {
            version.setIgnorePatch(true);
        }
        Version current = new Version(config.getString("version"));
        if(!current.validate()) {
            current.setIgnorePatch(true);
        }
        if(version.compareTo(current) > 0) {
            // Update to latest version
            plugin.getLogger().info(
                    "Updating to v" + plugin.getDescription().getVersion());
            update(current);
        }
    }

    /**
     * Update process.
     */
    private void update(final Version current) {
        // Run through update modules.
        for(UpdateModule module : modules) {
            if(module.shouldApplyUpdate(current)) {
                plugin.getLogger().info(
                        "Applying update for " + module.getTargetVersion());
                module.update();
            }
        }

        // Update version number in config.yml
        plugin.getConfig().set("version", plugin.getDescription().getVersion());
        plugin.saveConfig();
        plugin.getLogger().info(
                "Upgrade to " + plugin.getDescription().getVersion()
                        + " complete");
    }
}

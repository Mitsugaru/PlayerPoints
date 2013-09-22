package org.black_ixx.playerpoints.update;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.version.Version;

public abstract class UpdateModule implements Comparable<UpdateModule> {

    /**
     * Plugin reference
     */
    protected final PlayerPoints plugin;

    /**
     * The target version for the module.
     */
    protected Version targetVersion;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin reference
     */
    public UpdateModule(final PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the target version this module applies to.
     * 
     * @return Version for update module.
     */
    public Version getTargetVersion() {
        return targetVersion;
    }

    /**
     * Whether we should apply the update changes based on the current version
     * versus the target version.
     * 
     * @param current
     *            - Current version.
     * @return True if the current version is lower than the target version.
     */
    public boolean shouldApplyUpdate(final Version current) {
        return current.compareTo(targetVersion) < 0;
    }

    @Override
    public int compareTo(UpdateModule o) {
        return this.targetVersion.compareTo(o.getTargetVersion());
    }

    /**
     * Logic of this update.
     */
    public abstract void update();

}

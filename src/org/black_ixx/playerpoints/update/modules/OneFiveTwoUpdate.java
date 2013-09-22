package org.black_ixx.playerpoints.update.modules;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.services.version.Version;
import org.black_ixx.playerpoints.update.UpdateModule;

/**
 * Handles the update to 1.52 from previous versions.
 * 
 * @author Mitsugaru
 */
public class OneFiveTwoUpdate extends UpdateModule {

    /**
     * Constructor.
     * 
     * @param plugin
     *            - plugin instance.
     */
    public OneFiveTwoUpdate(PlayerPoints plugin) {
        super(plugin);
        targetVersion = new Version("1.52");
        targetVersion.setIgnorePatch(true);
    }

    @Override
    public void update() {
        final String unknownCommand = LocalizeConfig.getString(
                "message.unknownCommand",
                LocalizeNode.COMMAND_UNKNOWN.getDefaultValue());
        LocalizeConfig.set("message.unknownCommand", null);
        LocalizeConfig.set(LocalizeNode.COMMAND_UNKNOWN.getPath(),
                unknownCommand);
        LocalizeConfig.reload();
    }

}

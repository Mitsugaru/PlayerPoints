package org.black_ixx.playerpoints.update.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.version.Version;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.black_ixx.playerpoints.update.UpdateModule;

public class OneEightUpdate extends UpdateModule {
    
    private Map<String, Integer> cache = new HashMap<String, Integer>();

    public OneEightUpdate(PlayerPoints plugin) {
        super(plugin);
        targetVersion = new Version("1.8");
        targetVersion.setIgnorePatch(true);
    }

    @Override
    public void update() {
        // Translate player names to UUID
        StorageHandler storageHandler = plugin.getModuleForClass(StorageHandler.class);
        Collection<String> playerNames = storageHandler.getPlayers();
        for(String playerName : playerNames) {
            cache.put(playerName, storageHandler.getPoints(playerName));
        }
        //Rebuild if necessary
        storageHandler.destroy();
        storageHandler.build();
        // Add entries
        for(Map.Entry<String, Integer> entry : cache.entrySet()) {
            UUID id = plugin.translateNameToUUID(entry.getKey());
            storageHandler.setPoints(id.toString(), entry.getValue());
        }
    }

}

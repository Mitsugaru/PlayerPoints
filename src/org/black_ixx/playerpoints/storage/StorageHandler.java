package org.black_ixx.playerpoints.storage;

import java.util.Collection;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;

/**
 * Storage handler for getting / setting info between YAML, SQLite, and MYSQL.
 */
public class StorageHandler implements IStorage {
    /**
     * Generator for storage objects.
     */
    private StorageGenerator generator;
    /**
     * Current storage of player points information.
     */
    IStorage storage;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - PlayerPoints plugin instance.
     */
    public StorageHandler(PlayerPoints plugin) {
        final RootConfig config = plugin.getRootConfig();
        generator = new StorageGenerator(plugin);
        storage = generator
                .createStorageHandlerForType(config.getStorageType());
    }

    @Override
    public int getPoints(String name) {
        return storage.getPoints(name);
    }

    @Override
    public boolean setPoints(String name, int points) {
        return storage.setPoints(name, points);
    }

    @Override
    public boolean playerEntryExists(String name) {
        return storage.playerEntryExists(name);
    }

    @Override
    public Collection<String> getPlayers() {
        return storage.getPlayers();
    }
}

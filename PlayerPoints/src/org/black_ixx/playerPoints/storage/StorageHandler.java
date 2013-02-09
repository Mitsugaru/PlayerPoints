package org.black_ixx.playerPoints.storage;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.config.RootConfig;

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
    *           - PlayerPoints plugin instance.
    */
   public StorageHandler(PlayerPoints plugin) {
      final RootConfig config = plugin.getRootConfig();
      generator = new StorageGenerator(plugin);
      storage = generator.createStorageHandlerForType(config.getStorageType());
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
   public boolean playerInDatabase(String name) {
      return storage.playerInDatabase(name);
   }
}

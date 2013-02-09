package org.black_ixx.playerPoints.storage;

import org.black_ixx.playerPoints.PlayerPoints;

public class StorageGenerator {
   
   private PlayerPoints plugin;
   
   public StorageGenerator(PlayerPoints plugin) {
      this.plugin = plugin;
   }

   public IStorage createStorageHandlerForType(StorageType type) {
      IStorage storage = null;
      switch(type) {
      case YAML:
         storage = new YAMLStorage(plugin);
         break;
      case SQLITE:
         storage = new SQLiteStorage(plugin);
         break;
      case MYSQL:
         storage = new MySQLStorage(plugin);
         break;
      default:
         break;
      }
      return storage;
   }
}

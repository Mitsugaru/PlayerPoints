package org.black_ixx.playerPoints.storage.imports;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.storage.StorageGenerator;

public abstract class DatabaseImport {

   protected PlayerPoints plugin;
   
   protected StorageGenerator generator;

   public DatabaseImport(PlayerPoints plugin) {
      this.plugin = plugin;
      generator = new StorageGenerator(plugin);
   }

   abstract void doImport();
}

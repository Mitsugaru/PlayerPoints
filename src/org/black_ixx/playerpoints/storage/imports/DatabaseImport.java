package org.black_ixx.playerpoints.storage.imports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.StorageGenerator;

public abstract class DatabaseImport {

   protected PlayerPoints plugin;
   
   protected StorageGenerator generator;

   public DatabaseImport(PlayerPoints plugin) {
      this.plugin = plugin;
      generator = new StorageGenerator(plugin);
   }

   abstract void doImport();
}

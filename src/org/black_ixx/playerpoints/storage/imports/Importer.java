package org.black_ixx.playerpoints.storage.imports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.StorageType;

public class Importer {

   private PlayerPoints plugin;

   public Importer(PlayerPoints plugin) {
      this.plugin = plugin;
   }

   public void checkImport() {
      if(plugin.getRootConfig().importSQL && plugin.getRootConfig().getStorageType() == StorageType.MYSQL) {
         importSQL(plugin.getRootConfig().importSource);
         plugin.getConfig().set("mysql.import.use", false);
         plugin.saveConfig();
      }
   }

   /**
    * Imports from SQLite / YAML to MYSQL.
    * 
    * @param source
    *           - Type of storage to read from.
    */
   private void importSQL(StorageType source) {
      switch(source) {
      case YAML:
         YAMLImport yaml = new YAMLImport(plugin);
         yaml.doImport();
         break;
      case SQLITE:
         SQLiteImport sqlite = new SQLiteImport(plugin);
         sqlite.doImport();
      default:
         break;
      }
   }
}

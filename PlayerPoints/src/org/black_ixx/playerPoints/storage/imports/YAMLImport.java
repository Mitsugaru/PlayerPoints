package org.black_ixx.playerPoints.storage.imports;

import java.io.File;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.storage.IStorage;
import org.black_ixx.playerPoints.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLImport extends DatabaseImport {

   public YAMLImport(PlayerPoints plugin) {
      super(plugin);
   }

   @Override
   void doImport() {
      plugin.getLogger().info("Importing YAML to MySQL");
      IStorage mysql = generator.createStorageHandlerForType(StorageType.MYSQL);
      final ConfigurationSection config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsolutePath() + "/storage.yml"));
      final ConfigurationSection points = config.getConfigurationSection("Points");
      for(String key : points.getKeys(false)) {
         mysql.setPoints(key, points.getInt(key));
      }
   }

}

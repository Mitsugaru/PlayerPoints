package org.black_ixx.playerPoints.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.black_ixx.playerPoints.PlayerPoints;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLStorage implements IStorage {

   private PlayerPoints plugin;
   private File file;
   private YamlConfiguration config;

   public YAMLStorage(PlayerPoints pp) {
      plugin = pp;
      file = new File(plugin.getDataFolder().getAbsolutePath() + "/storage.yml");
      config = YamlConfiguration.loadConfiguration(file);
   }

   public void save() {
      // Set config
      try {
         // Save the file
         config.save(file);
      } catch(IOException e1) {
         plugin.getLogger().warning("File I/O Exception on saving storage.yml");
         e1.printStackTrace();
      }
   }

   public void reload() {
      try {
         config.load(file);
      } catch(FileNotFoundException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
      } catch(InvalidConfigurationException e) {
         e.printStackTrace();
      }
   }

   public boolean setPoints(String name, int points) {
      config.set("Points." + name, points);
      save();
      return true;
   }

   public int getPoints(String name) {
      int points = config.getInt("Points." + name, 0);
      return points;
   }

   @Override
   public boolean playerInDatabase(String name) {
      // TODO Auto-generated method stub
      return false;
   }
}

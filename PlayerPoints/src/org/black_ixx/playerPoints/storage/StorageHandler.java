package org.black_ixx.playerPoints.storage;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.config.RootConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Storage handler for getting / setting info between YAML, SQLite, and MYSQL.
 */
public class StorageHandler {
   /**
    * Plugin instance.
    */
   private PlayerPoints plugin;
   /**
    * Type of storage to use.
    */
   private StorageType backend;
   /**
    * SQLite reference.
    */
   private SQLite sqlite;
   /**
    * MYSQL reference.
    */
   private MySQL mysql;
   /**
    * YAML reference.
    */
   private YAMLStorage yaml;
   /**
    * Query for getting points.
    */
   private static final String GET_POINTS = "SELECT points FROM playerpoints WHERE playername=?;";
   /**
    * Query for adding a new player.
    */
   private static final String INSERT_PLAYER = "INSERT INTO playerpoints (points,playername) VALUES(?,?);";
   /**
    * Query for updating a player's point amount.
    */
   private static final String UPDATE_PLAYER = "UPDATE playerpoints SET points=? WHERE playername=?";

   /**
    * Constructor.
    * 
    * @param plugin
    *           - PlayerPoints plugin instance.
    */
   public StorageHandler(PlayerPoints plugin) {
      this.plugin = plugin;
      final RootConfig config = plugin.getRootConfig();
      this.backend = config.getStorageType();
      // Check for database type backend
      switch(backend) {
      case SQLITE: {
         sqlite = new SQLite(plugin.getLogger(), "", "storage", plugin.getDataFolder().getAbsolutePath());
         if(!sqlite.isTable("playerpoints")) {
            plugin.getLogger().info("Creating playerpoints table");
            try {
               sqlite.query("CREATE TABLE playerpoints (id INTEGER PRIMARY KEY, playername varchar(32) NOT NULL, points INTEGER NOT NULL, UNIQUE(playername));");
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create SQLite table.", e);
            }
         }
         break;
      }
      case MYSQL: {
         mysql = new MySQL(plugin.getLogger(), "", config.host, Integer.valueOf(config.port), config.database, config.user, config.password);
         if(!mysql.isTable("playerpoints")) {
            plugin.getLogger().info("Creating playerpoints table");
            try {
               mysql.query("CREATE TABLE playerpoints (id INT UNSIGNED NOT NULL AUTO_INCREMENT, playername varchar(32) NOT NULL, points INT NOT NULL, PRIMARY KEY(id), UNIQUE(playername));");
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create MySQL table.", e);
            }
         }
         break;
      }
      default: {
         yaml = new YAMLStorage(plugin);
         break;
      }
      }
      // Check import
      if(config.importSQL && backend == StorageType.MYSQL) {
         importSQL(config.importSource);
         plugin.getConfig().set("mysql.import.use", false);
         plugin.saveConfig();
      }
   }

   /**
    * Get the amount of points the given player has.
    * 
    * @param name
    *           - Name of player.
    * @return Points the player has.
    */
   public int getPoints(String name) {
      int points = 0;
      if(name == null || name.equals("")) {
         return points;
      }
      PreparedStatement statement = null;
      switch(backend) {
      case SQLITE: {
         try {
            statement = sqlite.prepare(GET_POINTS);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
         }
         break;
      }
      case MYSQL: {
         try {
            statement = mysql.prepare(GET_POINTS);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
         }
         break;
      }
      default: {
         return yaml.getPoints(name);
      }
      }
      if(statement == null) {
         return points;
      }
      ResultSet result = null;
      try {
         statement.setString(1, name);
         result = statement.executeQuery();
         if(result.next()) {
            points = result.getInt("points");
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
      } finally {
         if(result != null) {
            try {
               result.close();
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
            }
         }
         if(statement != null) {
            try {
               statement.close();
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
            }
         }
      }
      return points;
   }

   /**
    * Set the amount of points for the given player name.
    * 
    * @param name
    *           - Player name
    * @param points
    *           - Amount of points to set.
    * @return True if we were successful in editing, else false.
    */
   public boolean setPoints(String name, int points) {
      if(name == null || name.equals("")) {
         return false;
      }
      final boolean exists = playerInDatabase(name);
      PreparedStatement statement = null;
      switch(backend) {
      case SQLITE: {
         if(exists) {
            try {
               statement = sqlite.prepare(UPDATE_PLAYER);
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
            }
         } else {
            try {
               statement = sqlite.prepare(INSERT_PLAYER);
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
            }
         }
         break;
      }
      case MYSQL: {
         if(exists) {
            try {
               statement = mysql.prepare(UPDATE_PLAYER);
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
            }
         } else {
            try {
               statement = mysql.prepare(INSERT_PLAYER);
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
            }
         }
         break;
      }
      default: {
         yaml.setPoints(name, points);
         return true;
      }
      }
      try {
         statement.setInt(1, points);
         statement.setString(2, name);
         statement.executeUpdate();
         statement.close();
         return true;
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE, "SQLException on getPoints(" + name + ")", e);
      } finally {
         if(statement != null) {
            try {
               statement.close();
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
            }
         }
      }
      return false;
   }

   /**
    * Check if the given player name is in the database.
    * 
    * @param name
    *           - Player name to check.
    * @return True if player exists in database, else false.
    */
   public boolean playerInDatabase(String name) {
      boolean has = false;
      if(name == null || name.equals("")) {
         return has;
      }
      PreparedStatement statement = null;
      switch(backend) {
      case SQLITE: {
         try {
            statement = sqlite.prepare(GET_POINTS);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
         }
         break;
      }
      case MYSQL: {
         try {
            statement = mysql.prepare(GET_POINTS);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
         }
         break;
      }
      default: {
         return true;
      }
      }
      ResultSet result = null;
      try {
         statement.setString(1, name);
         result = statement.executeQuery();
         if(result.next()) {
            has = true;
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
      } finally {
         if(result != null) {
            try {
               result.close();
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
            }
         }
         if(statement != null) {
            try {
               statement.close();
            } catch(SQLException e) {
               plugin.getLogger().log(Level.SEVERE, "SQLException on playerInDatabase(" + name + ")", e);
            }
         }
      }
      return has;
   }

   /**
    * Imports from SQLite / YAML to MYSQL.
    * 
    * @param source
    *           - Type of storage to read from.
    */
   private void importSQL(StorageType source) {
      switch(source) {
      case SQLITE: {
         plugin.getLogger().info("Importing SQLite to MySQL");
         try {
            sqlite = new SQLite(plugin.getLogger(), "", "storage", plugin.getDataFolder().getAbsolutePath());
            final ResultSet query = sqlite.query("SELECT * FROM playerpoints");
            final PreparedStatement statement = mysql.prepare(INSERT_PLAYER);
            if(query.next()) {
               do {
                  statement.setInt(1, query.getInt("points"));
                  statement.setString(2, query.getString("playername"));
                  statement.addBatch();
               } while(query.next());
            }
            query.close();
            statement.executeBatch();
            statement.close();
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on importSQL(" + source.toString() + ")", e);
         }
         break;
      }
      case YAML: {
         plugin.getLogger().info("Importing YAML to MySQL");
         PreparedStatement statement = null;
         try {
            statement = mysql.prepare(INSERT_PLAYER);
            final File file = new File(plugin.getDataFolder().getAbsolutePath() + "/storage.yml");
            final ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection points = config.getConfigurationSection("Points");
            for(String key : points.getKeys(false)) {
               statement.setInt(1, points.getInt(key));
               statement.setString(2, key);
               statement.addBatch();
            }
            statement.executeBatch();
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on importSQL(" + source.toString() + ")", e);
         } finally {
            if(statement != null) {
               try {
                  statement.close();
               } catch(SQLException e) {
                  plugin.getLogger().log(Level.SEVERE, "SQLException on importSQL(" + source.toString() + ")", e);
               }
            }
         }
         break;
      }
      default: {
         break;
      }
      }
   }
}

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
         sqlite = new SQLite(plugin.getLogger(), " ", "storage", plugin.getDataFolder().getAbsolutePath());
         sqlite.open();
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
         mysql = new MySQL(plugin.getLogger(), " ", config.host, Integer.valueOf(config.port), config.database, config.user, config.password);
         mysql.open();
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
      ResultSet result = null;
      switch(backend) {
      case SQLITE: {
         try {
            statement = sqlite.prepare(GET_POINTS);
            statement.setString(1, name);
            result = sqlite.query(statement);
            if(result != null) {
               points = result.getInt("points");
            }
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      case MYSQL: {
         try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, name);
            result = sqlite.query(statement);
            if(result != null) {
               points = result.getInt("points");
            }
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      default: {
         points = yaml.getPoints(name);
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
      boolean value = false;
      final boolean exists = playerInDatabase(name);
      PreparedStatement statement = null;
      ResultSet result = null;
      switch(backend) {
      case SQLITE: {
         try {
            if(exists) {
               statement = mysql.prepare(UPDATE_PLAYER);
            } else {
               statement = mysql.prepare(INSERT_PLAYER);
            }
            statement.setInt(1, points);
            statement.setString(2, name);
            value = true;
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      case MYSQL: {
         try {
            if(exists) {
               statement = mysql.prepare(UPDATE_PLAYER);
            } else {
               statement = mysql.prepare(INSERT_PLAYER);
            }
            statement.setInt(1, points);
            statement.setString(2, name);
            result = mysql.query(statement);
            value = true;
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      default: {
         yaml.setPoints(name, points);
         value = true;
      }
      }
      return value;
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
      ResultSet result = null;
      switch(backend) {
      case SQLITE: {
         try {
            statement = sqlite.prepare(GET_POINTS);
            statement.setString(1, name);
            result = sqlite.query(statement);
            if(result.next()) {
               has = true;
            }
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      case MYSQL: {
         try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, name);
            result = mysql.query(statement);
            if(result.next()) {
               has = true;
            }
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      default: {
         return true;
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
         ResultSet query = null;
         ResultSet outerQuery = null;
         PreparedStatement statement = null;
         try {
            sqlite = new SQLite(plugin.getLogger(), " ", "storage", plugin.getDataFolder().getAbsolutePath());
            query = sqlite.query("SELECT * FROM playerpoints");
            statement = mysql.prepare(INSERT_PLAYER);
            if(query.next()) {
               do {
                  statement.setInt(1, query.getInt("points"));
                  statement.setString(2, query.getString("playername"));
                  statement.addBatch();
               } while(query.next());
            }
            query.close();
            outerQuery = mysql.query(statement);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on importSQL(" + source.toString() + ")", e);
         } finally {
            cleanup(query, null);
            cleanup(outerQuery, statement);
         }
         break;
      }
      case YAML: {
         plugin.getLogger().info("Importing YAML to MySQL");
         PreparedStatement statement = null;
         ResultSet result = null;
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
            result = mysql.query(statement);
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on importSQL(" + source.toString() + ")", e);
         } finally {
            cleanup(result, statement);
         }
         break;
      }
      default: {
         break;
      }
      }
   }

   /**
    * Cleanup the given resources.
    * 
    * @param result
    *           - ResultSet to close.
    * @param statement
    *           - Statement to close.
    */
   private void cleanup(ResultSet result, PreparedStatement statement) {
      if(result != null) {
         try {
            result.close();
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
         }
      }
      if(statement != null) {
         try {
            statement.close();
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
         }
      }
   }
}

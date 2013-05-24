package org.black_ixx.playerpoints.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.black_ixx.playerpoints.PlayerPoints;

/**
 * Represents a database type of storage.
 */
public abstract class DatabaseStorage implements IStorage {

   /**
    * Plugin instance.
    */
   protected PlayerPoints plugin;
   /**
    * Query for getting points.
    */
   protected static final String GET_POINTS = "SELECT points FROM playerpoints WHERE playername=?;";
   /**
    * Query for adding a new player.
    */
   protected static final String INSERT_PLAYER = "INSERT INTO playerpoints (points,playername) VALUES(?,?);";
   /**
    * Query for updating a player's point amount.
    */
   protected static final String UPDATE_PLAYER = "UPDATE playerpoints SET points=? WHERE playername=?";

   /**
    * Constructor.
    * 
    * @param plugin
    *           - PlayerPoints instance.
    */
   public DatabaseStorage(PlayerPoints plugin) {
      this.plugin = plugin;
   }

   /**
    * Cleanup the given resources.
    * 
    * @param result
    *           - ResultSet to close.
    * @param statement
    *           - Statement to close.
    */
   protected void cleanup(ResultSet result, PreparedStatement statement) {
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

   /**
    * Check if the given player name is in the database.
    * 
    * @param name
    *           - Player name to check.
    * @return True if player exists in database, else false.
    */
   public abstract boolean playerEntryExists(String name);
}

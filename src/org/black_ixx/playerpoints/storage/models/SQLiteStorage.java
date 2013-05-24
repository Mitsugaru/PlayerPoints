package org.black_ixx.playerpoints.storage.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.DatabaseStorage;

import lib.PatPeter.SQLibrary.SQLite;

public class SQLiteStorage extends DatabaseStorage {

   /**
    * SQLite reference.
    */
   private SQLite sqlite;

   /**
    * Constructor.
    * 
    * @param plugin
    *           - PlayerPoints instance.
    */
   public SQLiteStorage(PlayerPoints plugin) {
      super(plugin);
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
   }

   @Override
   public int getPoints(String name) {
      int points = 0;
      if(name == null || name.equals("")) {
         return points;
      }
      PreparedStatement statement = null;
      ResultSet result = null;
      try {
         statement = sqlite.prepare(GET_POINTS);
         statement.setString(1, name);
         result = sqlite.query(statement);
         if(result != null && result.next()) {
            points = result.getInt("points");
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
      } finally {
         cleanup(result, statement);
      }
      return points;
   }

   @Override
   public boolean setPoints(String name, int points) {
      boolean value = false;
      if(name == null || name.equals("")) {
         return value;
      }
      final boolean exists = playerEntryExists(name);
      PreparedStatement statement = null;
      ResultSet result = null;
      try {
         if(exists) {
            statement = sqlite.prepare(UPDATE_PLAYER);
         } else {
            statement = sqlite.prepare(INSERT_PLAYER);
         }
         statement.setInt(1, points);
         statement.setString(2, name);
         result = sqlite.query(statement);
         value = true;
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
      } finally {
         cleanup(result, statement);
      }
      return value;
   }

   @Override
   public boolean playerEntryExists(String name) {
      boolean has = false;
      if(name == null || name.equals("")) {
         return has;
      }
      PreparedStatement statement = null;
      ResultSet result = null;
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
      return has;
   }

}

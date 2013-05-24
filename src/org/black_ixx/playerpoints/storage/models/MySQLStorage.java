package org.black_ixx.playerpoints.storage.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.DatabaseStorage;

/**
 * Storage handler for MySQL source.
 * 
 * @author Mitsugaru
 */
public class MySQLStorage extends DatabaseStorage {

   /**
    * MYSQL reference.
    */
   private MySQL mysql;

   /**
    * Constructor.
    * 
    * @param plugin
    *           - Plugin instance.
    */
   public MySQLStorage(PlayerPoints plugin) {
      super(plugin);
      mysql = new MySQL(plugin.getLogger(), " ", plugin.getRootConfig().host,
            Integer.valueOf(plugin.getRootConfig().port),
            plugin.getRootConfig().database, plugin.getRootConfig().user,
            plugin.getRootConfig().password);
      mysql.open();
      if(!mysql.isTable("playerpoints")) {
         plugin.getLogger().info("Creating playerpoints table");
         try {
            mysql.query("CREATE TABLE playerpoints (id INT UNSIGNED NOT NULL AUTO_INCREMENT, playername varchar(32) NOT NULL, points INT NOT NULL, PRIMARY KEY(id), UNIQUE(playername));");
         } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                  "Could not create MySQL table.", e);
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
         statement = mysql.prepare(GET_POINTS);
         statement.setString(1, name);
         result = mysql.query(statement);
         if(result != null && result.next()) {
            points = result.getInt("points");
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE,
               "Could not create getter statement.", e);
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
            statement = mysql.prepare(UPDATE_PLAYER);
         } else {
            statement = mysql.prepare(INSERT_PLAYER);
         }
         statement.setInt(1, points);
         statement.setString(2, name);
         result = mysql.query(statement);
         value = true;
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE,
               "Could not create setter statement.", e);
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
         statement = mysql.prepare(GET_POINTS);
         statement.setString(1, name);
         result = mysql.query(statement);
         if(result.next()) {
            has = true;
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE,
               "Could not create player check statement.", e);
      } finally {
         cleanup(result, statement);
      }
      return has;
   }

   @Override
   public Collection<String> getPlayers() {
      Collection<String> players = new HashSet<String>();

      PreparedStatement statement = null;
      ResultSet result = null;
      try {
         statement = mysql.prepare(GET_PLAYERS);
         result = mysql.query(statement);

         while(result.next()) {
            String name = result.getString("playername");
            if(name != null) {
               players.add(name);
            }
         }
      } catch(SQLException e) {
         plugin.getLogger().log(Level.SEVERE,
               "Could not create get players statement.", e);
      } finally {
         cleanup(result, statement);
      }

      return players;
   }

}

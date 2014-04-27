package org.black_ixx.playerpoints.storage.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
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
     * Number of attempts to reconnect before completely failing an operation.
     */
    private int retryLimit = 10;
    /**
     * Current retry count.
     */
    private int retryCount = 0;
    /**
     * Skip operation flag.
     */
    private boolean skip = false;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public MySQLStorage(PlayerPoints plugin) {
        super(plugin);
        retryLimit = plugin.getModuleForClass(RootConfig.class).retryLimit;
        connect();
        if(!mysql.isTable("playerpoints")) {
            build();
        }
    }

    @Override
    public int getPoints(String id) {
        int points = 0;
        if(id == null || id.equals("")) {
            return points;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, id);
            result = mysql.query(statement);
            if(result != null && result.next()) {
                points = result.getInt("points");
            }
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create getter statement.", e);
            retryCount++;
            connect();
            if(!skip) {
                points = getPoints(id);
            }
        } finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        return points;
    }

    @Override
    public boolean setPoints(String id, int points) {
        boolean value = false;
        if(id == null || id.equals("")) {
            return value;
        }
        final boolean exists = playerEntryExists(id);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            if(exists) {
                statement = mysql.prepare(UPDATE_PLAYER);
            } else {
                statement = mysql.prepare(INSERT_PLAYER);
            }
            statement.setInt(1, points);
            statement.setString(2, id);
            result = mysql.query(statement);
            value = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create setter statement.", e);
            retryCount++;
            connect();
            if(!skip) {
                value = setPoints(id, points);
            }
        } finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        return value;
    }

    @Override
    public boolean playerEntryExists(String id) {
        boolean has = false;
        if(id == null || id.equals("")) {
            return has;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, id);
            result = mysql.query(statement);
            if(result.next()) {
                has = true;
            }
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create player check statement.", e);
            retryCount++;
            connect();
            if(!skip) {
                has = playerEntryExists(id);
            }
        } finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        return has;
    }
    
    @Override
    public boolean removePlayer(String id) {
        boolean deleted = false;
        if(id == null || id.equals("")) {
            return deleted;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(REMOVE_PLAYER);
            statement.setString(1, id);
            result = mysql.query(statement);
            deleted = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create player remove statement.", e);
            retryCount++;
            connect();
            if(!skip) {
                deleted = playerEntryExists(id);
            }
        } finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        return deleted;
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
            retryCount++;
            connect();
            if(!skip) {
                players.clear();
                players.addAll(getPlayers());
            }
        } finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        return players;
    }

    /**
     * Connect to MySQL database. Close existing connection if one exists.
     */
    private void connect() {
        if(mysql != null) {
            mysql.close();
        }
        RootConfig config = plugin.getModuleForClass(RootConfig.class);
        mysql = new MySQL(plugin.getLogger(), " ", config.host,
                Integer.valueOf(config.port), config.database, config.user,
                config.password);
        if(retryCount < retryLimit) {
            mysql.open();
        } else {
            plugin.getLogger().severe(
                    "Tried connecting to MySQL " + retryLimit
                            + " times and could not connect.");
            plugin.getLogger()
                    .severe("It may be in your best interest to restart the plugin / server.");
            retryCount = 0;
            skip = true;
        }
    }

    @Override
    public boolean destroy() {
        boolean success = false;
        plugin.getLogger().info("Creating playerpoints table");
        try {
            mysql.query("DROP TABLE playerpoints;");
            success = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not drop MySQL table.", e);
        }
        return success;
    }

    @Override
    public boolean build() {
        boolean success = false;
        plugin.getLogger().info("Creating playerpoints table");
        try {
            mysql.query("CREATE TABLE playerpoints (id INT UNSIGNED NOT NULL AUTO_INCREMENT, playername varchar(36) NOT NULL, points INT NOT NULL, PRIMARY KEY(id), UNIQUE(playername));");
            success = true;
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not create MySQL table.", e);
        }
        return success;
    }

}

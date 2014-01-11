package org.black_ixx.playerpoints.storage.exports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import lib.PatPeter.SQLibrary.MySQL;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Handles MySQL to YAML export.
 * 
 * @author Mitsugaru
 */
public class MySQLExport extends DatabaseExport {
    
    /**
     * MYSQL reference.
     */
    private MySQL mysql;

    public MySQLExport(PlayerPoints plugin) {
        super(plugin);
        mysql = new MySQL(plugin.getLogger(), " ", plugin.getRootConfig().host,
                Integer.valueOf(plugin.getRootConfig().port),
                plugin.getRootConfig().database, plugin.getRootConfig().user,
                plugin.getRootConfig().password);
        mysql.open();
    }

    @Override
    void doExport() {
        IStorage yaml = generator.createStorageHandlerForType(StorageType.YAML);
        ResultSet query = null;
        try {
            query = mysql.query("SELECT * FROM playerpoints");
            if(query.next()) {
                do {
                    yaml.setPoints(query.getString("playername"),
                            query.getInt("points"));
                } while(query.next());
            }
            query.close();
        } catch(SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "SQLException on SQLite export", e);
        } finally {
            mysql.close();
        }
    }

}

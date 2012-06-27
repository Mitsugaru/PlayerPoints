/**
 * SQLite
 * Inherited subclass for reading and writing to and from an SQLite file.
 *
 * Modified by Mitsugaru. Separated out query and select so that it would
 * stop that nasty bug of always outputting "not return ResultSet"
 *
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package org.black_ixx.playerPoints.storage.SQLibrary;

/*
 * SQLite
 */
import java.io.File;
import java.sql.DatabaseMetaData;

/*
 * Both
 */
//import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends Database {
	public String location;
	public String name;
	private File sqlFile;
	private static final int timeout = 1000;
	private int count;

	public SQLite(Logger log, String prefix, String name, String location) {
		super(log,prefix,"[SQLite] ");
		this.name = name;
		this.location = location;
		File folder = new File(this.location);
		if (this.name.contains("/") ||
				this.name.contains("\\") ||
				this.name.endsWith(".db")) {
			this.writeError("The database name can not contain: /, \\, or .db", true, null);
		}
		if (!folder.exists()) {
			if(!folder.mkdir())
			{
				this.writeError(" Could not create folder @ '" + folder.getAbsolutePath(), true, null);
			}
		}

		sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
		count = 0;
	}

	@Override
	protected boolean initialize() {
		try {
		  Class.forName("org.sqlite.JDBC");

		  return true;
		} catch (ClassNotFoundException e) {
		  this.writeError("You need the SQLite library " + e, true, e);
		  return false;
		}
	}

	@Override
	public Connection open() {
		if (initialize()) {
			try {
			  return DriverManager.getConnection("jdbc:sqlite:" +
					  	   sqlFile.getAbsolutePath());
			} catch (SQLException e) {
			  this.writeError("SQLite exception on initialize " + e, true, e);
			}
		}
		return null;
	}

	@Override
	public void close() {
		Connection connection = this.open();
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException ex) {
				this.writeError("Error on Connection close: " + ex.getMessage(), true, ex);
			}
	}

	@Override
	public Connection getConnection() {
		if (this.connection == null)
			return open();
		return this.connection;
	}

	@Override
	public boolean checkConnection() {
		Connection connection = this.open();
		if (connection != null)
			return true;
		return false;
	}

	public Query select(String query)
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;

		try {
			connection = this.open();
			statement = connection.createStatement();

			switch (this.getStatement(query)) {
				case SELECT:
					result = statement.executeQuery(query);
					return new Query(connection, statement, result);

				default:
					statement.executeQuery(query);
					return new Query(connection, statement, result);
			}
		} catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				return retryResult(query);
				//this.writeError("",false);
			} else {
				this.writeError("Error at SQL Query: " + ex.getMessage(), false, ex);
			}

		}
		return null;
	}

	public void retryQuery(String query) {
		boolean passed = false;
		Connection connection = open();
		Statement statement = null;
		count = 0;

		while (!passed && count < timeout) {
			try {
				statement = connection.createStatement();
				final ResultSet rs = statement.executeQuery(query);
				rs.close();
				statement.close();
				connection.close();
				passed = true;
			} catch (SQLException ex) {
				if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
					passed = false;
					count++;
					this.writeError("Locked",false, ex);
				} else {
					if(!(ex.toString().contains("not return ResultSet")))
						this.writeError("Error at SQL Query: " + ex.getMessage(), false, ex);
				}
			}
		}
		if(count >= timeout)
		{
			this.writeError("Failed to write to SQLite database. Timed out.",true, null);
		}
	}

	public void standardQuery(String query) {
		Connection connection = null;
		Statement statement = null;

		try {
			connection = this.open();
			statement = connection.createStatement();
			final ResultSet rs = statement.executeQuery(query);
			rs.close();
			statement.close();
			connection.close();
			}
		catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				//this.writeError("Locked",false);
				retryQuery(query);
			} else {
				if(!(ex.toString().contains("not return ResultSet")))
					this.writeError("Error at SQL Query: " + ex.getMessage(), false, ex);
			}
		}
	}

	@Override
	public PreparedStatement prepare(String query) {
		Connection connection = null;
		 PreparedStatement ps = null;
		try
	    {
	        connection = open();
	        ps = connection.prepareStatement(query);
	        return ps;
	    } catch(SQLException e) {
	        if(!e.toString().contains("not return ResultSet"))
	        	this.writeError("Error in SQL prepare() query: " + e.getMessage(), false, e);
	    }
	    return ps;
	}

	@Override
	public boolean createTable(String query) {
		try {
			if(query == null)
			{
				this.writeError("SQL Create Table query null", true, null);
				return false;
			}
			if (query.equals("") || query == null) {
				this.writeError("SQL Create Table query empty.", true, null);
				return false;
			}
			final Connection connection = open();
			Statement statement = null;
			statement = connection.createStatement();
			statement.execute(query);
			statement.close();
			connection.close();
			return true;
		} catch (SQLException ex){
			this.writeError(ex.getMessage(), true, ex);
			return false;
		}
	}

	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData dbm = null;
		try {
			boolean has = false;
			dbm = this.open().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next())
			{
				has = true;
			}
			tables.close();
			return has;
		} catch (SQLException e) {
			this.writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true, e);
			return false;
		}
	}

	@Override
	public boolean wipeTable(String table) {
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error at Wipe Table: table, " + table + ", does not exist", true, null);
				return false;
			}
			final PreparedStatement statement = prepare("DELETE FROM ?;");
			statement.setString(1, table);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException ex) {
			if (!(ex.getMessage().toLowerCase().contains("locking") ||
				ex.getMessage().toLowerCase().contains("locked")) &&
				!ex.toString().contains("not return ResultSet"))
					this.writeError("Error at SQL Wipe Table Query: " + ex, false, ex);
			return false;
		}
	}

	/*
	 * Retries a result.
	 *
	 * @param query The SQL query to retry.
	 * @return The SQL query result.
	 */
	public Query retryResult(String query) {
		boolean passed = false;
		Connection connection = open();
		Statement statement = null;
		ResultSet result = null;

		while (!passed) {
			try {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
				passed = true;
				return new Query(connection, statement, result);
			} catch (SQLException ex) {
				if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
					passed = false;
				} else {
					this.writeError("Error at SQL Query: " + ex.getMessage(), false, ex);
				}
			}
		}

		return null;
	}

	/*@Override
	ResultSet query(String query) {
		// INFO Auto-generated method stub
		// method replaced by separated method
		return null;
	}*/
}
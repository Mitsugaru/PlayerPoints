package org.black_ixx.playerPoints.storage.SQLibrary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public Query(Connection connection, Statement statement,
            ResultSet resultSet) {
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
    }

    public ResultSet getResult() {
        return resultSet;
    }

    public void closeQuery() throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }
}

package cz.vutbr.fit.pdb.db;

import java.lang.Exception;
import java.sql.*;
import java.util.List;

import lombok.extern.java.Log;
import oracle.jdbc.pool.OracleDataSource;

/*
 * Wrapper over database connection.
 *
 * This class implements singleton design pattern.
 */
@Log
public class DBConnection {
    private static DBConnection dbConnection = null;
    private boolean isConnected = false;
    private Connection connection;

    /**
     * Creates new DBConnection.
     */
    private DBConnection() {

    }

    /**
     * Creates instance of DBConnection. Use connect() to connect to specific database.
     *
     * @return DBConnection
     */
    public static DBConnection getInstance() {
        if (dbConnection == null){
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    /**
     * Connects to the database. When there is already a connection, use disconnect() first.
     * @param host Host name.
     * @param port Port number.
     * @param serviceName Service name.
     * @param username User.
     * @param password Password.
     * @return True, when connection succeeded, false otherwise.
     */
    public boolean connect(String host, String port, String serviceName, String username, String password) {
        if (!isConnected) {
            String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
            try {
                OracleDataSource ods = new OracleDataSource();
                ods.setURL(url);
                ods.setUser(username);
                ods.setPassword(password);

                connection = ods.getConnection();

                isConnected = true;
            } catch (Exception ex) {
                log.severe("Connection to: " + url + " failed: " + ex);
                isConnected = false;
                throw new RuntimeException(ex);
            }
        }
        log.info("DB successfully connnected.");
        return true;
    }

    /**
     * Closes the current connection.
     * @return True, when disconnect succeded, false otherwise.
     */
    public boolean disconnect(){
        if (isConnected){
            try {
                connection.close();
            } catch (SQLException ex) {
                log.severe("SQLException: " + ex);
                return false;
            }
        }
        return true;
    }

    public Connection getConnection() {
        return connection;
    }


    /**
     * Executes the given queries.
     * @param queries
     */
    public void execute(List<String> queries) {
        if (!isConnected) {
            log.severe("Cannot execute queries on DB without connection.");
            throw new RuntimeException();
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            log.info("Cannot disable autocommit: " + ex);
        }

        queries.forEach((String query) -> {
            try {
                execute(query);
            } catch (Exception ex) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException sqlEx) {
                    log.info("Cannot enable autocommit: " + sqlEx);
                }
            }
        });

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            log.info("Cannot commit: " + ex);
        }
    }

    /**
     * Executes the given query.
     * @param query
     * @return Result of the query.
     * @throws SQLException
     */
    public void execute(String query) {
        if (!isConnected) {
            log.severe("Cannot execute query on DB without connection.");
            throw new RuntimeException();
        }

        try {
            try (Statement stmt = connection.createStatement()) {
                try {
                    stmt.executeQuery(query);
                }
                catch (SQLException ex) {
                    log.severe("DB query failed: Execute SQL query exception: " + ex + " : " +query);
                    throw new RuntimeException(ex);
                }

            }
        }
        catch (SQLException ex) {
            log.severe("DB query failed: Create SQL statement exception: " + ex + " : " + query);
            throw new RuntimeException(ex);
        }
    }

    public Integer getMaxId(String table){
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT max(id) AS max FROM " + table
        )) {
            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    return rset.getInt("max");
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex + stmt.toString());
                return 0;
            }
        }
        catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            return 0;
        }
        return 0;
    }
}

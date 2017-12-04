package cz.vutbr.fit.pdb.db;

import java.io.IOException;
import java.lang.Exception;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
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
    public static DBConnection create() {
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
                return false;
            }
        }
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

    public void initDB(String filePath) {
        if (!isConnected) {
            log.severe("Cannot initialize database without connection.");
            return;
        }

        List<String> queries;
        String script;

        try {
            script = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            log.severe("SQLException: " + ex);
            return;
        }

        queries = Arrays.asList(script.split(";"));

        queries.forEach((String query) -> {
            try {
                try (Statement stmt = connection.createStatement()) {
                    try {
                        stmt.executeQuery(query);
                    }
                    catch (SQLException ex) {
                        log.severe("Execute SQL query exception: " + ex);
                    }

                }
            }
            catch (Exception ex) {
                log.severe("Create SQL statement exception: " + ex);
            }
        });
        log.info("DB successfully initialized");
    }
}

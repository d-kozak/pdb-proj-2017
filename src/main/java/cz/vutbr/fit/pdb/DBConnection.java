package cz.vutbr.fit.pdb;

import java.io.IOException;
import java.lang.Exception;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

/*
 * Connection to the database.
 *
 * This class implements singleton design pattern.
 */
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
                System.err.println("Exception: " + ex.getMessage());
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
                System.err.println("SQLException: " + ex.getMessage());
                return false;
            }
        }
        return true;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initDB(String filePath) {
        List<String> queries;
        String script;

        try {
            script = new String(Files.readAllBytes(Paths.get("readMe.txt")));
        } catch (IOException ex) {
            System.err.println("Cannot read file: " + filePath + ": " + ex.getMessage());
            return;
        }

        queries = Arrays.asList(script.split(";"));

        queries.forEach((query) -> {
            try {
                try (Statement stmt = connection.createStatement()) {
                    try {
                        stmt.executeQuery(query);
                    }
                    catch (SQLException ex) {
                        System.err.println("SQLException: " + ex.getMessage());
                    }

                }
            }
            catch (SQLException ex) {
                System.err.println("SQLException: " + ex.getMessage());
            }
        });
    }
}

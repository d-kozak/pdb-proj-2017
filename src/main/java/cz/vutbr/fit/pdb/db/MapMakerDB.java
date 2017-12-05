package cz.vutbr.fit.pdb.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import lombok.extern.java.Log;


/*
 * Wrapper over database connection for our specific application.
 *
 * This class implements singleton design pattern.
 */
@Log
public class MapMakerDB {
    private static MapMakerDB mapMakerDB = null;
    private static DBConnection dbConnection = DBConnection.create();
    private static Connection connection = dbConnection.getConnection();

    private MapMakerDB() {

    }

    public static MapMakerDB create() {
        if (mapMakerDB == null) {
            mapMakerDB = new MapMakerDB();
        }
        return mapMakerDB;
    }

    /**
     * Runs SQL script on the given filePath.
     * @param filePath
     */
    public void initDB(String filePath) {
        List<String> queries;
        String script;

        try {
            script = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            log.severe("Reading of initialization script failed: " + ex);
            return;
        }

        queries = Arrays.asList(script.trim().split(";"));

        try {
            dbConnection.execute(queries);
        } catch (Exception ex) {
            log.severe("Init DB failed: " + ex);
            return;
        }
        log.info("DB successfully initialized");
    }
}

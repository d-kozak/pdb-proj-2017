package cz.vutbr.fit.pdb.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import lombok.extern.java.Log;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;


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

        if (!initPictures()) {
            log.severe("DB pictures initialization failed!");
            return;
        }
        log.info("DB successfully initialized");
    }

    private boolean initPictures() {
        boolean res = false;
        res |= initPicture(
          "Brno flag",
          "flag",
          Date.valueOf(LocalDate.now()),
          1,
          "src/resources/brno-flag.jpg"
        );
        res |= initPicture(
                "Brno Petrov",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-petrov.jpg"
        );
        res |= initPicture(
                "Brno square",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-square.jpg"
        );
        res |= initPicture(
                "Praha flag",
                "flag",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-flag.jpg"
        );
        return res;
    }

    /**
     * Inserts a new image to the database.
     * @param description Description of the image.
     * @param type Type - 'flag' or 'normal'.
     * @param createdAt Date of creation.
     * @param spatialEntityId Id of referenced entity.
     * @param imgPath Path to the image.
     * @return Boolean value.
     */
    private boolean initPicture(String description, String type,
                                Date createdAt, Integer spatialEntityId, String imgPath) {
        Integer id = getMaxId("Picture") + 1;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            log.info("Cannot disable autocommit: " + ex);
        }

        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Picture(id, description, pictureType, createdAt, spatialEntityId, img) " +
                    "VALUES(?, ?, ?, ?, ?, ORDSYS.ORDIMAGE.init())"
            )) {
                stmt.setInt(1, id);
                stmt.setString(2, description);
                stmt.setString(3, type);
                stmt.setDate(4, Date.valueOf(LocalDate.now()));
                stmt.setInt(5, spatialEntityId);
                try {
                    stmt.executeUpdate();
                }
                catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        }
        catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            return false;
        }

        OrdImage imgProxy = null;
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT img from Picture WHERE id = ? FOR UPDATE"
            )) {
                stmt.setInt(1, id);
                try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()){
                    rset.next();
                    imgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                }
                catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        }
        catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            return false;
        }

        try {
            imgProxy.loadDataFromFile(imgPath);
            imgProxy.setProperties();
        } catch (SQLException  | IOException ex) {
            log.severe("Failed to load img: " + ex);
            return false;
        }

        try {
            try (OraclePreparedStatement stmt = (OraclePreparedStatement) connection.prepareStatement(
                    "UPDATE Picture SET img = ? WHERE id = ?"
            )) {
                stmt.setORAData(1, imgProxy);
                stmt.setInt(2, id);
                try {
                    stmt.executeUpdate();
                }
                catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        }
        catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            return false;
        }

        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Picture p SET p.img_si = SI_STILLIMAGE(p.img.getContent()) " +
                    "WHERE p.id = ?"
            )) {
                stmt.setInt(1, id);
                try {
                    stmt.executeUpdate();
                    String updateSql2 = "UPDATE Picture SET " +
                            "img_ac = SI_AVERAGECOLOR(img_si), " +
                            "img_ch = SI_COLORHISTOGRAM(img_si), " +
                            "img_pc = SI_POSITIONALCOLOR(img_si), " +
                            "img_tx = SI_TEXTURE(img_si) " +
                            "WHERE id = " + id;
                    stmt.executeUpdate(updateSql2);
                }
                catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        }
        catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            return false;
        }

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            log.info("Cannot commit: " + ex);
        }
        return true;
    }

    private static Integer getMaxId(String table){
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

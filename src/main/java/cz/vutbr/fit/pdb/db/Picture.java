package cz.vutbr.fit.pdb.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import lombok.extern.java.Log;

@Log
public class Picture {
    private static DBConnection dbConnection = DBConnection.create();
    private static Connection connection = dbConnection.getConnection();

    private Picture() {

    }

    public static ObservableList<Image> loadImagesFor(Integer entityId) {
        return loadPicturesFor(entityId, "normal");
    }

    public static Image loadFlagFor(Integer entityId) {
        ObservableList<Image> images = loadPicturesFor(entityId, "flag");
        if (!images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public static void deleteImage(Integer id) {
        deletePicture(id, "normal");
    }

    public static void deleteFlag(Integer id) {
        deletePicture(id, "flag");
    }

    private static void deletePicture(Integer id, String type){
        dbConnection.execute("DELETE FROM Picture " +
                "id = " + id + " and type = " + type
        );
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
    public static boolean insertPicture(String description, String type,
                                 Date createdAt, Integer spatialEntityId, String imgPath) {
        Integer id = dbConnection.getMaxId("Picture") + 1;
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

    public ObservableList<Image> findSmiliar(Integer id)
    {
        ObservableList<Image> images = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT dest.*, SI_ScoreByFtrList(" +
                        "new SI_FeatureList(src.img_ac, 0.3, src.img_ch,0.3, src.img_pc, 0.1, src.img_tx, 0.3), dest.img_si) " +
                        "AS similarity FROM Picture src, Picture dest " +
                        "WHERE (src.id <> dest.id) AND src.id = ? " +
                        "ORDER BY similarity ASC"
        )) {
            stmt.setInt(1, id);
            try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                while (rset.next()) {
                    OrdImage imgProxy = null;
                    imgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                    if (imgProxy != null) {
                        try {
                            if (imgProxy.getDataInByteArray() != null) {
                                BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
                                images.add(SwingFXUtils.toFXImage(bufferedImg, null));
                            }
                        } catch (IOException ex) {
                            log.severe("Loading similar picture failed: " + ex);
                        }
                    }
                }
            } catch (SQLException ex) {
                log.severe("Find similar picture failed: Execute SQL statement exception: " + ex);
            }
        } catch (SQLException ex) {
            log.severe("Find similar picture failed: Create SQL statement exception: " + ex);
        }
        return images;
    }


    private static ObservableList<Image> loadPicturesFor(Integer entityId, String type) {
        ObservableList<Image> images = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Picture " +
                        "WHERE spatialEntityId = ? and pictureType = ? " +
                        "ORDER BY createdAt DESC"
        )) {
            stmt.setInt(1, entityId);
            stmt.setString(2, type);
            try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                while(rset.next()) {
                    OrdImage imgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                    if (imgProxy != null) {
                        try {
                            if (imgProxy.getDataInByteArray() != null) {
                                BufferedImage img = ImageIO.read(
                                        new ByteArrayInputStream(imgProxy.getDataInByteArray())
                                );
                                images.add(SwingFXUtils.toFXImage(img, null));
                            }
                        } catch (IOException ex) {
                            log.severe("Load image failed: " + ex);
                        }
                    }
                }
            } catch (SQLException ex) {
                log.severe("Load image: Execute SQL query exception: " + ex);
            }
        } catch (SQLException ex) {
            log.severe("Load image: Create SQL statement exception: " + ex);
        }
        return images;
    }
}

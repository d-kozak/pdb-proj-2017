package cz.vutbr.fit.pdb.db;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import lombok.extern.java.Log;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;

@Log
public class Picture {
    private static DBConnection dbConnection = DBConnection.getInstance();
    private static Connection connection = dbConnection.getConnection();

    private Picture() {

    }

    public static ObservableList<EntityImage> loadImagesFor(Integer entityId) {
        return loadPicturesFor(entityId, "normal");
    }

    public static EntityImage loadFlagFor(Integer entityId) {
        ObservableList<EntityImage> images = loadPicturesFor(entityId, "flag");
        if (!images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public static void deleteImage(EntityImage entityImage) {
        deletePicture(entityImage, "normal");
    }

    public static void deleteFlag(EntityImage entityImage) {
        deletePicture(entityImage, "flag");
    }

    private static void deletePicture(EntityImage entityImage, String type) {
        dbConnection.execute("DELETE FROM Picture " +
                "WHERE id = " + entityImage.getId() + " and pictureType = " + type
        );
    }


    /**
     * Inserts new flag ot the database.
     * @param entityImage
     * @param spatialEntityId
     * @return
     */
    public static boolean insertFlag(EntityImage entityImage, Integer spatialEntityId) {
        return insertPicture(entityImage, spatialEntityId, "flag");
    }


    /**
     * INserts new iamge to the database.
     * @param entityImage
     * @param spatialEntityId
     * @return
     */
    public static boolean insertImage(EntityImage entityImage, Integer spatialEntityId) {
        return insertPicture(entityImage, spatialEntityId, "normal");
    }

    /**
     * Inserts a new image to the database.
     * @param entityImage
     * @param spatialEntityId
     * @param pictureType
     * @return
     */
    private static boolean insertPicture(EntityImage entityImage, Integer spatialEntityId, String pictureType) {
        Integer id = dbConnection.getMaxId("Picture") + 1;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            log.info("Cannot disable autocommit: " + ex);
            throw new RuntimeException(ex);
        }

        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Picture(id, description, pictureType, createdAt, spatialEntityId, img) " +
                            "VALUES(?, ?, ?, ?, ?, ORDSYS.ORDIMAGE.init())"
            )) {
                stmt.setInt(1, id);
                stmt.setString(2, entityImage.getDescription());
                stmt.setString(3, pictureType);
                stmt.setDate(4, Date.valueOf(entityImage.getTime()));
                stmt.setInt(5, spatialEntityId);
                try {
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        } catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        OrdImage imgProxy = null;
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT img from Picture WHERE id = ? FOR UPDATE"
            )) {
                stmt.setInt(1, id);
                try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                    rset.next();
                    imgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                } catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        try {
            imgProxy.loadDataFromFile(entityImage.getUrl());
            imgProxy.setProperties();
        } catch (SQLException | IOException ex) {
            log.severe("Failed to load img: " + entityImage.getUrl() + " : " + ex);
            throw new RuntimeException(ex);
        }

        try {
            try (OraclePreparedStatement stmt = (OraclePreparedStatement) connection.prepareStatement(
                    "UPDATE Picture SET img = ? WHERE id = ?"
            )) {
                stmt.setORAData(1, imgProxy);
                stmt.setInt(2, id);
                try {
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    return false;
                }
            }
        } catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
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
                } catch (SQLException ex) {
                    log.severe("Init picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Init picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            log.info("Cannot commit: " + ex);
        }
        entityImage.setId(id);
        return true;
    }

    public static boolean pictureToFlag(EntityImage picture, Integer spatialEntityId) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            log.info("Cannot disable autocommit: " + ex);
            throw new RuntimeException(ex);
        }


        try {
            Integer oldFlag = null;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id FROM Picture WHERE pictureType = ? and spatialEntityId = ? "
            )) {
                stmt.setString(1, "flag");
                stmt.setInt(2, spatialEntityId);
                try (ResultSet rset = stmt.executeQuery()){
                    if (rset.next()) {
                        oldFlag = rset.getInt("id");
                    }
                } catch (SQLException ex) {
                    log.severe("Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Picture SET pictureType = ? WHERE id = ? "
            )) {
                stmt.setString(1, "flag");
                stmt.setInt(2, picture.getId());
                try {
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    log.severe("Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
            if (oldFlag != null) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE Picture SET pictureType = ? WHERE id = ? "
                )) {
                    stmt.setString(1, "normal");
                    stmt.setInt(2, oldFlag);
                    try {
                        stmt.executeUpdate();
                    } catch (SQLException ex) {
                        log.severe("Execute SQL query exception: " + ex);
                        throw new RuntimeException(ex);
                    }
                }
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            log.info("Cannot commit: " + ex);
        }
        return true;
    }

    public static boolean makeImageMonochrome(Integer id) {
        return modifyPicture("contentFormat=monochrome fileformat=png", id);
    }

    private static boolean modifyPicture(String modification, Integer srcId) {
        Integer dstId = dbConnection.getMaxId("Picture") + 1;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            log.info("Cannot disable autocommit: " + ex);
            throw new RuntimeException(ex);
        }

        // retrieve ORDImage object of a source image
        OrdImage srcImageProxy = null;
        String description;
        String type;
        Date createdAt;
        Integer spatialEntityId;

        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT description, pictureType, createdAt, spatialEntityId, img FROM Picture WHERE id = ?"
            )) {
                stmt.setInt(1, srcId);
                try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                    rset.next();
                    srcImageProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                    description = rset.getString("description");
                    type = rset.getString("pictureType");
                    createdAt = rset.getDate("createdAt");
                    spatialEntityId = rset.getInt("spatialEntityId");
                } catch (SQLException ex) {
                    log.severe("Load picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Load picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        // insert a new record with an empty ORDImage object
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO Picture(id, description, pictureType, createdAt, spatialEntityId, img) " +
                            "VALUES(?, ?, ?, ?, ?, ORDSYS.ORDIMAGE.init())"
            )) {
                stmt.setInt(1, dstId);
                stmt.setString(2, description);
                stmt.setString(3, type);
                stmt.setDate(4, createdAt);
                stmt.setInt(5, spatialEntityId);
                try {
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    log.severe("Insert picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Insert picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        // retrieve the previously created ORDImage object for updating
        OrdImage dstImgProxy = null;
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT img from Picture WHERE id = ? FOR UPDATE"
            )) {
                stmt.setInt(1, dstId);
                try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                    rset.next();
                    dstImgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                } catch (SQLException ex) {
                    log.severe("Select picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Select picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        // perform conversion (processing occurs on the Oracle Database)
        try {
            srcImageProxy.processCopy(modification, dstImgProxy);
        } catch (SQLException ex) {
            log.severe("Img processing failed: " + ex);
            throw new RuntimeException(ex);
        }

        // save the target image
        try {
            try (OraclePreparedStatement stmt = (OraclePreparedStatement) connection.prepareStatement(
                    "UPDATE Picture SET img = ? WHERE id = ? "
            )) {
                stmt.setORAData(1, dstImgProxy);
                stmt.setInt(2, dstId);
                try {
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    log.severe("Save picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("Save picture failed: Create SQL statement exception: " + ex);
            return false;
        }

        // update the target image with StillImage object and features
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Picture p SET p.img_si = SI_STILLIMAGE(p.img.getContent()) " +
                            "WHERE p.id = ?"
            )) {
                stmt.setInt(1, dstId);
                try {
                    stmt.executeUpdate();
                    String updateSql2 = "UPDATE Picture SET " +
                            "img_ac = SI_AVERAGECOLOR(img_si), " +
                            "img_ch = SI_COLORHISTOGRAM(img_si), " +
                            "img_pc = SI_POSITIONALCOLOR(img_si), " +
                            "img_tx = SI_TEXTURE(img_si) " +
                            "WHERE id = " + dstId;
                    stmt.executeUpdate(updateSql2);
                } catch (SQLException ex) {
                    log.severe("Update picture failed: Execute SQL query exception: " + ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (SQLException ex) {
            log.severe("update picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            log.info("Cannot commit: " + ex);
        }
        return true;
    }

    private static ObservableList<EntityImage> loadPicturesFor(Integer entityId, String type) {
        ObservableList<EntityImage> images = FXCollections.observableArrayList();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Picture " +
                        "WHERE spatialEntityId = ? and pictureType = ? " +
                        "ORDER BY createdAt DESC"
        )) {
            stmt.setInt(1, entityId);
            stmt.setString(2, type);
            try (OracleResultSet rset = (OracleResultSet) stmt.executeQuery()) {
                while (rset.next()) {
                    OrdImage imgProxy = (OrdImage) rset.getORAData("img", OrdImage.getORADataFactory());
                    if (imgProxy != null) {
                        try {
                            if (imgProxy.getDataInByteArray() != null) {
                                BufferedImage img = ImageIO.read(
                                        new ByteArrayInputStream(imgProxy.getDataInByteArray())
                                );
                                WritableImage writableImage = SwingFXUtils.toFXImage(img, null);
                                EntityImage entityImage = new EntityImage();
                                entityImage.setImage(writableImage);
                                entityImage.setId(rset.getInt("id"));
                                entityImage.setDescription(rset.getString("description"));
                                entityImage.setTime(rset.getDate("createdAt").toLocalDate());
                                images.add(entityImage);
                            }
                        } catch (IOException ex) {
                            log.severe("Load image failed: " + ex);
                            throw new RuntimeException(ex);
                        }
                    }
                }
            } catch (SQLException ex) {
                log.severe("Load image: Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Load image: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return images;
    }

    public ObservableList<Image> findSmiliar(Integer id) {
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
                            throw new RuntimeException(ex);
                        }
                    }
                }
            } catch (SQLException ex) {
                log.severe("Find similar picture failed: Execute SQL statement exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Find similar picture failed: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return images;
    }
}

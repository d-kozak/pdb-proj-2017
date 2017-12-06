package cz.vutbr.fit.pdb.db;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import cz.vutbr.fit.pdb.entity.geometry.PolygonGeometry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.java.Log;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;
import oracle.spatial.geometry.JGeometry;

import oracle.jdbc.OraclePreparedStatement;

import javax.imageio.ImageIO;

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


    private static ObservableList<Entity> entities = FXCollections.observableArrayList();

    static {
        loadEntities();
    }

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    public static ObservableList<Entity> getEntities() {
        return entities;
    }

    private static void loadEntities() {
		try (Statement stmt = connection.createStatement()) {
			try (ResultSet rset = stmt.executeQuery("SELECT * FROM SpatialEntity")) {
				while (rset.next()) {
					Entity entity = new Entity();
					entity.setId(rset.getInt("id"));
					entity.setName(rset.getString("name"));

					entity.setFrom(rset.getDate("validFrom").toLocalDate());
					entity.setTo(rset.getDate("validTo").toLocalDate());

					String entityType = rset.getString("entityType");
					switch (entityType) {
						case "country":
                            byte[] countryData = rset.getBytes("geometry");
                            JGeometry jGeometryCountry = JGeometry.load(countryData);
                            double[] countryCoords = jGeometryCountry.getOrdinatesArray();
                            Integer countryCoordsCount = jGeometryCountry.getNumPoints();
                            ObservableList<Point> countryPoints = FXCollections.observableArrayList();
                            for (Integer i = 0; i < countryCoordsCount * 2; i += 2) {
                                countryPoints.add(new Point(countryCoords[i], countryCoords[i + 1]));
                            }
                            entity.setGeometry(new PolygonGeometry(countryPoints));
							break;
						case "river":
						    byte[] riverData = rset.getBytes("geometry");
						    JGeometry jGeometryRiver = JGeometry.load(riverData);
						    double[] riverCoords = jGeometryRiver.getOrdinatesArray();
						    Integer riverCoordsCount = jGeometryRiver.getNumPoints();
							ObservableList<Point> riverPoints = FXCollections.observableArrayList();
							for (Integer i = 0; i < riverCoordsCount * 2; i += 2) {
                                riverPoints.add(new Point(riverCoords[i], riverCoords[i + 1]));
                            }
							entity.setGeometry(new LineGeometry(riverPoints));
							break;
						case "place":
							byte[] placeData = rset.getBytes("geometry");
							JGeometry jGeometry = JGeometry.load(placeData);
							double[] pointCoords = jGeometry.getPoint();
							entity.setGeometry(
								new PointGeometry(new Point(pointCoords[0], pointCoords[1]))
							);
							break;
						default:
							log.severe("Unknown spatial entity: " + entityType);
					}
					entity.setDescription(loadDescriptionFor(entity.getId()));
					entity.setFlag(loadFlagFor(entity.getId()));
					entity.setImages(loadImagesFor(entity.getId()));
					addEntity(entity);
				}
			} catch (SQLException ex) {
				log.severe("Load entities: Execute SQL query exception: " + ex);
			} catch (Exception ex){
				log.severe("Load entities: Exception: " + ex);
			}
		} catch (SQLException ex) {
			log.severe("Load entities: Create SQL statement exception: " + ex);
		}
    }

    private static String loadDescriptionFor(Integer entityId) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT * FROM Description " +
            "WHERE spatialEntityId = ? " +
            "ORDER BY validTo DESC " +
            "FETCH FIRST 1 row ONLY"
        )) {
            stmt.setInt(1, entityId);
            try (ResultSet rset = stmt.executeQuery()) {
                if(rset.next()) {
                    return rset.getString("description");
                }
            } catch (SQLException ex) {
                log.severe("Load description: Execute SQL query exception: " + ex);
            }
        } catch (SQLException ex) {
            log.severe("Load description: Create SQL statement exception: " + ex);
        }
        return "Description.";
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

    private static ObservableList<Image> loadImagesFor(Integer entityId) {
        return loadPicturesFor(entityId, "normal");
    }

    private static Image loadFlagFor(Integer entityId) {
        ObservableList<Image> images = loadPicturesFor(entityId, "flag");
        if (!images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    /**
     * Runs SQL script on the given filePath.
     * @param filePath Path to the initialization script.
     */
    public boolean initDB(String filePath) {
        List<String> queries;
        String script;

        try {
            script = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            log.severe("Reading of initialization script failed: " + ex);
            return false;
        }

        queries = Arrays.asList(script.trim().split(";"));

        try {
            dbConnection.execute(queries);
        } catch (Exception ex) {
            log.severe("Init DB failed: " + ex);
            return false;
        }

        if (!initPictures()) {
            log.severe("DB pictures initialization failed!");
            return false;
        }
        log.info("DB successfully initialized");
        return true;
    }

    private boolean initPictures() {
        boolean res = false;
        res |= insertPicture(
          "Brno flag",
          "flag",
          Date.valueOf(LocalDate.now()),
          1,
          "src/resources/brno-flag.jpg"
        );
        res |= insertPicture(
                "Brno Petrov",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-petrov.jpg"
        );
        res |= insertPicture(
                "Brno square",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-square.jpg"
        );
        res |= insertPicture(
                "Praha flag",
                "flag",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-flag.jpg"
        );
        res |= insertPicture(
                "Praha bridge",
                "normal",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-bridge.jpg"
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
    public boolean insertPicture(String description, String type,
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

}

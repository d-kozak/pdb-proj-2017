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
import lombok.extern.java.Log;
import oracle.spatial.geometry.JGeometry;


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
					entity.setFlag(Picture.loadFlagFor(entity.getId()));
					entity.setImages(Picture.loadImagesFor(entity.getId()));
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
        res |= Picture.insertPicture(
          "Brno flag",
          "flag",
          Date.valueOf(LocalDate.now()),
          1,
          "src/resources/brno-flag.jpg"
        );
        res |= Picture.insertPicture(
                "Brno Petrov",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-petrov.jpg"
        );
        res |= Picture.insertPicture(
                "Brno square",
                "normal",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-square.jpg"
        );
        res |= Picture.insertPicture(
                "Praha flag",
                "flag",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-flag.jpg"
        );
        res |= Picture.insertPicture(
                "Praha bridge",
                "normal",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-bridge.jpg"
        );
        return res;
    }


}

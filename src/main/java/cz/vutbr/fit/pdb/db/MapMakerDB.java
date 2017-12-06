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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.extern.java.Log;
import oracle.spatial.geometry.JGeometry;


/*
 * Wrapper over database connection for our specific application.
 *
 * This class implements singleton design pattern.
 */
@Log
public class MapMakerDB {
    private static final int SRID = 0;
    private static final int DIMENSION = 2;

    private static MapMakerDB mapMakerDB = null;
    private static DBConnection dbConnection = DBConnection.getInstance();
    private static Connection connection = dbConnection.getConnection();

    private MapMakerDB() {

    }

    public static MapMakerDB getInstance() {
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
							for (Integer i = 0; i < riverCoordsCount * DIMENSION; i += DIMENSION) {
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
     * Inserts the given entity to the database and sets correct ID.
     * Only description, dates and geometry is inserted. Images and flag are NOT inserted!
     * @param entity
     */
    private static void insertEntity(Entity entity) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType) VALUES( " +
                        "?, ?, ?, ?, ?, ?)"
        )) {
            entity.setId(dbConnection.getMaxId("spatialEntity") + 1);
            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getName());
            try {
                stmt.setObject(3, JGeometry.storeJS(connection, geometryToJGeometry(entity.getGeometry())));
            } catch (Exception ex) {
                log.severe("Insert entity: Conversion to JGeometry: " + ex);
                return;
            }
            stmt.setDate(4, Date.valueOf(entity.getFrom()));
            stmt.setDate(5, Date.valueOf(entity.getTo()));
            stmt.setString(6, geometryToType(entity.getGeometry()));
        } catch (SQLException ex) {
            log.severe("Insert entity: Create SQL statement exception: " + ex);
        }
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
        res |= Picture.insertFlag(
          "Brno flag",
          Date.valueOf(LocalDate.now()),
          1,
          "src/resources/brno-flag.jpg"
        );
        res |= Picture.insertImage(
                "Brno Petrov",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-petrov.jpg"
        );
        res |= Picture.insertImage(
                "Brno square",
                Date.valueOf(LocalDate.now()),
                1,
                "src/resources/brno-square.jpg"
        );
        res |= Picture.insertFlag(
                "Praha flag",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-flag.jpg"
        );
        res |= Picture.insertImage(
                "Praha bridge",
                Date.valueOf(LocalDate.now()),
                2,
                "src/resources/praha-bridge.jpg"
        );
        Picture.makeImageMonochrome(2);
        return res;
    }

    private static String geometryToType(EntityGeometry geometry) {
        if (geometry instanceof PointGeometry) {
            return "place";
        } else if (geometry instanceof CircleGeometry) {
            return "place";
        } else if (geometry instanceof PolygonGeometry) {
            return "country";
        } else if (geometry instanceof LineGeometry) {
            return "river";
        }
        log.severe("Unknwon geometry.");
        return "unknown";
    }

    private static JGeometry geometryToJGeometry(EntityGeometry geometry){
        if (geometry instanceof PointGeometry) {
            return new JGeometry(
                    ((PointGeometry) geometry).getX(),
                    ((PointGeometry) geometry).getY(),
                    SRID
            );
        } else if (geometry instanceof CircleGeometry) {
            CircleGeometry cGeometry = (CircleGeometry) geometry;
            double radius = cGeometry.getRadius();
            return new JGeometry(
                    3,
                    SRID,
                    new int[]{1, 1003, 4},
                    new double[]{
                            cGeometry.getCenter().getX(), cGeometry.getCenter().getY() - radius,
                            cGeometry.getCenter().getX() + radius, cGeometry.getCenter().getY(),
                            cGeometry.getCenter().getX(), cGeometry.getCenter().getY() + radius,
                    }
            );
        } else if (geometry instanceof PolygonGeometry){
            ObservableList<Point> points = ((PolygonGeometry) geometry).getPoints();
            double coords[] = new double[points.size()];
            for ( int i = 0; i < points.size(); i++) {
                coords[i * DIMENSION] = points.get(i).getX();
                coords[i * DIMENSION + 1] = points.get(i).getY();
            };
            return new JGeometry(
                    3,
                    SRID,
                    new int[]{1, 1003, 1}, // exterior polygon
                    coords
            );
        } else if (geometry instanceof LineGeometry) {
            ObservableList<Point> points = ((LineGeometry) geometry).getPoints();
            double coords[] = new double[points.size() * DIMENSION];
            for ( int i = 0; i < points.size(); i++) {
                coords[i * DIMENSION] = points.get(i).getX();
                coords[i * DIMENSION + 1] = points.get(i).getY();
            };
            return new JGeometry(
                    2,
                    SRID,
                    new int[]{1, 2, 1},
                    coords
            );
        }
        log.severe("Unknown geometry: " + geometry.getClass());
        return null;
    }
}

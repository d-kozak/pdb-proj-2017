package cz.vutbr.fit.pdb.db;


import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.geometry.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;
import oracle.spatial.geometry.JGeometry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


/*
 * Wrapper over database connection for our specific application.
 *
 * This class implements singleton design pattern.
 */
@Log
public class MapMakerDB {
    public static final int SRID = 0;
    public static final int DIMENSION = 2;

    private static MapMakerDB mapMakerDB = null;
    private static DBConnection dbConnection = DBConnection.getInstance();

    private MapMakerDB() {

    }

    /**
     * Returns (singleton) instance of this class.
     * @return
     */
    public static MapMakerDB getInstance() {
        if (mapMakerDB == null) {
            mapMakerDB = new MapMakerDB();
        }
        return mapMakerDB;
    }

    private static ObservableList<Entity> entities;

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Returns all loaded entities.
     * @return
     */
    public static ObservableList<Entity> getEntities() {
        return loadEntities();
    }

    /**
     * Loads all entities from the database.
     * @return
     */
    private static ObservableList<Entity> loadEntities() {
        if (entities == null)
            entities = FXCollections.observableArrayList();
        entities.clear();
        try (Statement stmt = DBConnection.getInstance()
                                          .getConnection()
                                          .createStatement()) {
            try (ResultSet rset = stmt.executeQuery("SELECT * FROM SpatialEntity")) {
                while (rset.next()) {
                    Entity entity = new Entity();
                    entity.setId(rset.getInt("id"));
                    entity.setName(rset.getString("name"));
                    entity.setColor(Color.web(rset.getString("color")));

                    entity.setFrom(rset.getDate("validFrom")
                                       .toLocalDate());
                    entity.setTo(rset.getDate("validTo")
                                     .toLocalDate());

                    String entityType = rset.getString("entityType");
                    switch (entityType) {
                        case "country":
                            byte[] countryData = rset.getBytes("geometry");
                            JGeometry jGeometryCountry = JGeometry.load(countryData);
                            double[] countryCoords = jGeometryCountry.getOrdinatesArray();
                            int[] elemInfo = jGeometryCountry.getElemInfo();
                            int dimensions = jGeometryCountry.getDimensions();
                            // We do not want the last point - it's same as the frist one in the DB
                            // and Entity does not store the first one also as the last one
                            Integer countryCoordsCount = jGeometryCountry.getNumPoints() - 1;
                            if (elemInfo.length > 3) {
                                // We want elements only from the first part.
                                countryCoordsCount = (elemInfo[3] / dimensions);
                            }
                            ObservableList<Point> countryPoints = FXCollections.observableArrayList();
                            for (Integer i = 0; i < countryCoordsCount * dimensions; i += dimensions) {
                                countryPoints.add(new Point(countryCoords[i], countryCoords[i + 1]));
                            }
                            entity.setGeometry(new PolygonGeometry(countryPoints));
                            break;
                        case "countryRec":
                            byte[] countryRecData = rset.getBytes("geometry");
                            JGeometry jGeometryRecCountry = JGeometry.load(countryRecData);
                            double[] countryRecCoords = jGeometryRecCountry.getOrdinatesArray();
                            ObservableList<Point> countryRecPoints = FXCollections.observableArrayList();
                            if (countryRecCoords.length < 4) {
                                log.severe("Not enough rectangle coords.");
                                throw new RuntimeException();
                            }
                            countryRecPoints.add(new Point(countryRecCoords[0], countryRecCoords[1]));
                            countryRecPoints.add(new Point(countryRecCoords[2], countryRecCoords[3]));
                            entity.setGeometry(new RectangleGeometry(countryRecPoints));
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
                        case "largePlace":
                            byte[] circlePlaceData = rset.getBytes("geometry");
                            JGeometry jGeometryCircle = JGeometry.load(circlePlaceData);
                            double[] circleCoords = jGeometryCircle.getOrdinatesArray();
                            double xCoord = circleCoords[0];
                            double yCoord = circleCoords[5];
                            double radius = circleCoords[5] - circleCoords[1];
                            entity.setGeometry(
                                    new CircleGeometry(
                                            new Point(xCoord, yCoord),
                                            radius
                                    )
                            );
                            break;
                        default:
                            log.severe("Unknown spatial entity: " + entityType);
                    }
                    entity.setDescription(Description.loadDescriptionFor(entity.getId()));
                    entity.setFlag(Picture.loadFlagFor(entity.getId()));
                    entity.setImages(Picture.loadImagesFor(entity.getId()));
                    addEntity(entity);
                }
            } catch (SQLException ex) {
                log.severe("Load entities: Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            } catch (Exception ex) {
                log.severe("Load entities: Exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Load entities: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return entities;
    }

    /**
     * Inserts the given entity to the database and sets correct ID.
     * Only description, dates and geometry is inserted. Images and flag are NOT inserted!
     *
     * @param entity
     */
    public static Entity insertEntity(Entity entity) {
        EntityGeometry geo = entity.getGeometry();
        JGeometry jGeo = null;
        try (PreparedStatement stmt = DBConnection.getInstance()
                                                  .getConnection()
                                                  .prepareStatement(
                                                          "INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType, color) VALUES( " +
                                                                  "?, ?, ?, ?, ?, ?, ?)"
                                                  )) {
            entity.setId(DBConnection.getInstance()
                                     .getMaxId("spatialEntity") + 1);
            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getName());
            try {
                jGeo = Spatial.geometryToJGeometry(geo);
                stmt.setObject(3, JGeometry.storeJS(DBConnection.getInstance()
                                                                .getConnection(), jGeo));
            } catch (Exception ex) {
                log.severe("Insert entity: Conversion to JGeometry: " + ex);
                throw new RuntimeException(ex);
            }
            stmt.setDate(4, Date.valueOf(entity.getFrom()));
            stmt.setDate(5, Date.valueOf(entity.getTo()));
            stmt.setString(6, Spatial.geometryToType(entity.getGeometry()));
            stmt.setString(7, entity.getColor()
                                    .toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.severe("Insert entity: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        if ((!(geo instanceof RectangleGeometry)) && (geo instanceof PolygonGeometry)) {
            double[] countryCoords = jGeo.getOrdinatesArray();
            int dimensions = jGeo.getDimensions();
            // We do not want the last point - it's same as the frist one in the DB
            // and Entity does not store the first one also as the last one
            Integer countryCoordsCount = jGeo.getNumPoints() - 1;
            int[] elemInfo = jGeo.getElemInfo();
            if (elemInfo.length > 3) {
                // We want elements only from the first part.
                countryCoordsCount = (elemInfo[3] / dimensions);
            }
            ObservableList<Point> countryPoints = FXCollections.observableArrayList();
            for (Integer i = 0; i < countryCoordsCount * dimensions; i += dimensions) {
                countryPoints.add(new Point(countryCoords[i], countryCoords[i + 1]));
            }
            geo = new PolygonGeometry(countryPoints);
        }
        entity.setGeometry(geo);
        return entity;
    }

    /**
     * Updates the specified field of the entity with value in 'entity'.
     * @param entity
     * @param field
     * @return
     */
    public static Entity updateEntity(Entity entity, String field) {
        if (field == "description") {
            Description.addDescription(entity);
            return entity;
        }
        if (field == "from") {
            field = "validFrom";
        } else if (field == "to") {
            field = "validTo";
        }
        EntityGeometry geo = entity.getGeometry();
        JGeometry jGeo = Spatial.geometryToJGeometry(geo);

        try (PreparedStatement stmt = DBConnection.getInstance()
                                                  .getConnection()
                                                  .prepareStatement(
                                                          "UPDATE SpatialEntity SET " + field + " = ? WHERE id = ?"
                                                  )) {
            stmt.setInt(2, entity.getId());

            switch (field) {
                case "name":
                    stmt.setString(1, entity.getName());
                    break;
                case "validFrom":
                    stmt.setDate(1, Date.valueOf(entity.getFrom()));
                    break;
                case "validTo":
                    stmt.setDate(1, Date.valueOf(entity.getTo()));
                    break;
                case "color":
                    stmt.setString(1, entity.getColor()
                                            .toString());
                    break;
                case "geometry":
                    try {
                        stmt.setObject(1, JGeometry.storeJS(DBConnection.getInstance()
                                                                        .getConnection(), jGeo));
                    } catch (Exception ex) {
                        log.severe("Update entity: Conversion to JGeometry: " + ex);
                        throw new RuntimeException(ex);
                    }
                    break;
                default:
                    log.severe("Unknown field to update: " + field);
                    throw new RuntimeException();
            }

            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.severe("Update entity: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        if (field == "geometry"){
            if ((!(geo instanceof RectangleGeometry)) && (geo instanceof PolygonGeometry)) {
                double[] countryCoords = jGeo.getOrdinatesArray();
                int dimensions = jGeo.getDimensions();
                // We do not want the last point - it's same as the frist one in the DB
                // and Entity does not store the first one also as the last one
                Integer countryCoordsCount = jGeo.getNumPoints() - 1;
                ObservableList<Point> countryPoints = FXCollections.observableArrayList();
                int[] elemInfo = jGeo.getElemInfo();
                if (elemInfo.length > 3) {
                    // We want elements only from the first part.
                    countryCoordsCount = (elemInfo[3] / dimensions);
                }
                for (Integer i = 0; i < countryCoordsCount * dimensions; i += dimensions) {
                    countryPoints.add(new Point(countryCoords[i], countryCoords[i + 1]));
                }
                geo = new PolygonGeometry(countryPoints);
            }
            entity.setGeometry(geo);
        }
        return entity;
    }

    /**
     * Removes the given entity from the database.
     * @param entity
     */
    public static void deleteEntity(Entity entity) {
        dbConnection.execute("DELETE FROM SpatialEntity " +
                "WHERE id = " + entity.getId()
        );
    }

    /**
     * For demo purpose.
     * @return
     */
    private boolean initPictures() {
        boolean res = false;
        EntityImage entityImage = new EntityImage();
        entityImage.setDescription("Brno flag");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/brno-flag.jpg");
        res |= Picture.insertFlag(entityImage, 1);

        entityImage = new EntityImage();
        entityImage.setDescription("Brno Petrov");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/brno-petrov.jpg");
        res |= Picture.insertImage(entityImage, 1);

        entityImage = new EntityImage();
        entityImage.setDescription("Brno square");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/brno-square.jpg");
        res |= Picture.insertImage(entityImage, 1);

        entityImage = new EntityImage();
        entityImage.setDescription("Praha flag");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/praha-flag.jpg");
        res |= Picture.insertFlag(entityImage, 2);

        entityImage = new EntityImage();
        entityImage.setDescription("Praha bridge");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/praha-bridge.jpg");
        res |= Picture.insertImage(entityImage, 2);

        entityImage = new EntityImage();
        entityImage.setDescription("CR flag");
        entityImage.setTime(LocalDate.now());
        entityImage.setUrl("src/resources/cr-flag.jpg");
        res |= Picture.insertFlag(entityImage, 4);

        return res;
    }

    /**
     * Runs SQL script on the given filePath of DDL .
     *
     * @param clearFilePath Path to the cleansing script. (exceptions skipped)
     * @param filePath      Path to the initialization script.
     */
    public boolean initDB(String clearFilePath, String filePath) {
        List<String> clearQueries;
        List<String> queries;
        String clearScript;
        String script;

        try {
            clearScript = new String(Files.readAllBytes(Paths.get(clearFilePath)));
            script = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            log.severe("Reading of initialization script failed: " + ex);
            throw new RuntimeException(ex);
        }

        clearQueries = Arrays.asList(clearScript.trim()
                                                .split(";"));

        try {
            dbConnection.execute(clearQueries);
        } catch (Exception ex) {
        }

        queries = Arrays.asList(script.trim()
                                      .split(";"));

        try {
            dbConnection.execute(queries);
        } catch (Exception ex) {
            log.severe("Init DB failed: " + ex);
            throw new RuntimeException(ex);
        }

        if (!initPictures()) {
            log.severe("DB pictures initialization failed!");
            return false;
        }
        log.info("DB successfully initialized");

        loadEntities();
        return true;
    }
}

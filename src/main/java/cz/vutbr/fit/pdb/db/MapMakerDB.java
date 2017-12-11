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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


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

    private MapMakerDB() {

    }

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

    public static ObservableList<Entity> getEntities() {
        return loadEntities();
    }

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
                            int dimensions = jGeometryCountry.getDimensions();
                            // We do not want the last point - it's same as the frist one in the DB
                            // and Entity does not store the first one also as the last one
                            Integer countryCoordsCount = jGeometryCountry.getNumPoints() - 1;
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
                    entity.setDescription(loadDescriptionFor(entity.getId()));
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

    private static String loadDescriptionFor(Integer entityId) {
        try (PreparedStatement stmt = DBConnection.getInstance()
                                                  .getConnection()
                                                  .prepareStatement(
                                                          "SELECT * FROM Description " +
                                                                  "WHERE spatialEntityId = ? " +
                                                                  "ORDER BY validTo DESC " +
                                                                  "FETCH FIRST 1 row ONLY"
                                                  )) {
            stmt.setInt(1, entityId);
            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    return rset.getString("description");
                }
            } catch (SQLException ex) {
                log.severe("Load description: Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Load description: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return "Description.";
    }

    private static boolean addDescription(Entity entity) {
        Integer id = DBConnection.getInstance()
                .getMaxId("spatialEntity") + 1;
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) " +
                           "VALUES(?, ?, ?, ?, ?)"
                )) {
            stmt.setInt(1, id);
            stmt.setString(2, entity.getDescription());
            stmt.setDate(3, Date.valueOf(entity.getFrom()));
            stmt.setDate(4, Date.valueOf(entity.getTo()));
            stmt.setInt(5, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.severe("Update entity: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return true;
    }

    /**
     * Inserts the given entity to the database and sets correct ID.
     * Only description, dates and geometry is inserted. Images and flag are NOT inserted!
     *
     * @param entity
     */
    public static void insertEntity(Entity entity) {
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
                stmt.setObject(3, JGeometry.storeJS(DBConnection.getInstance()
                                                                .getConnection(), geometryToJGeometry(entity.getGeometry())));
            } catch (Exception ex) {
                log.severe("Insert entity: Conversion to JGeometry: " + ex);
                throw new RuntimeException(ex);
            }
            stmt.setDate(4, Date.valueOf(entity.getFrom()));
            stmt.setDate(5, Date.valueOf(entity.getTo()));
            stmt.setString(6, geometryToType(entity.getGeometry()));
            stmt.setString(7, entity.getColor()
                                    .toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.severe("Insert entity: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
    }

    public static void updateEntity(Entity entity, String field) {
        if (field == "description") {
            addDescription(entity);
        }
        if (field == "from") {
            field = "validFrom";
        } else if (field == "to") {
            field = "validTo";
        }
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
                                                                        .getConnection(), geometryToJGeometry(entity.getGeometry())));
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
    }

    public static void deleteEntity(Entity entity) {
        dbConnection.execute("DELETE FROM SpatialEntity " +
                "WHERE id = " + entity.getId()
        );
    }

    private static JGeometry geometryToJGeometry(EntityGeometry geometry) {
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
                            cGeometry.getCenter().getX(), cGeometry.getCenter()
                                                                   .getY() - radius,
                            cGeometry.getCenter().getX(), cGeometry.getCenter()
                                                                   .getY() + radius,
                            cGeometry.getCenter()
                                     .getX() + radius, cGeometry.getCenter().getY()
                    }
            );
        } else if (geometry instanceof RectangleGeometry) {
            List<Point> points = new ArrayList<>(((RectangleGeometry) geometry).getPoints());
            if (points.size() < 2) {
                log.severe("Not enough rectangle points.");
                throw new RuntimeException();
            }
            double coords[] = new double[4];
            coords[0] = points.get(0)
                              .getX();
            coords[1] = points.get(0)
                              .getY();
            coords[2] = points.get(1)
                              .getX();
            coords[3] = points.get(1)
                              .getY();
            return new JGeometry(
                    3,
                    SRID,
                    new int[]{1, 1003, 3}, // exterior polygon
                    coords
            );
        } else if (geometry instanceof PolygonGeometry) {
            List<Point> points = new ArrayList<>(((PolygonGeometry) geometry).getPoints());
            if (are_clockwise_points(points)) {
                for (int i = 0; i < points.size() / 2; ++i) {
                    Point tmp = points.get(i);
                    points.set(i, points.get(points.size() - 1 - i));
                    points.set(points.size() - 1 - i, tmp);
                }
            }
            // DB needs the first points also as the last one. In Entity, each points is unique.
            if (!points.isEmpty()) {
                points.add(points.get(0));
            }
            double coords[] = new double[points.size() * DIMENSION];
            for (int i = 0; i < points.size(); i++) {
                coords[i * DIMENSION] = points.get(i)
                                              .getX();
                coords[i * DIMENSION + 1] = points.get(i)
                                                  .getY();
            }
            ;
            return new JGeometry(
                    3,
                    SRID,
                    new int[]{1, 1003, 1}, // exterior polygon
                    coords
            );
        } else if (geometry instanceof LineGeometry) {
            ObservableList<Point> points = ((LineGeometry) geometry).getPoints();
            double coords[] = new double[points.size() * DIMENSION];
            for (int i = 0; i < points.size(); i++) {
                coords[i * DIMENSION] = points.get(i)
                                              .getX();
                coords[i * DIMENSION + 1] = points.get(i)
                                                  .getY();
            }
            ;
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

    public static ObservableList<Integer> entitiesContainingPoint(Point point) {
        ObservableList<Integer> list = FXCollections.observableArrayList();
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                    "SELECT e.id " +
                       "FROM spatialEntity e " +
                       "WHERE SDO_CONTAINS(e.geometry, " +
                       "  SDO_GEOMETRY(2001, NULL, " +
                       "     SDO_POINT_TYPE(?, ?, NULL), " +
                       "     NULL, NULL) " +
                       "  ) = 'TRUE'"
                )) {
            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY());
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    list.add(rset.getInt("id"));
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return list;
    }

    public static double getArea(Entity entity) {
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "SELECT SDO_GEOM.SDO_AREA(e.geometry,0.005) AS area " +
                           "FROM spatialEntity e WHERE e.id = ?"
                )) {
            stmt.setInt(1, entity.getId());
            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    return rset.getDouble("area");
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return -1;
    }

    public static double getCircumference(Entity entity) {
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "SELECT SDO_GEOM.SDO_LENGTH(e.geometry,0.005) AS length " +
                                "FROM spatialEntity e WHERE e.id = ?"
                )) {
            stmt.setInt(1, entity.getId());
            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    return rset.getDouble("length");
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return -1;
    }

    /**
     * Returns names of entities inside the given entity.
     * @param entity
     * @return
     */
    public static ObservableList<String> entitiesInside(Entity entity) {
        ObservableList<String> list = FXCollections.observableArrayList();
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "SELECT outerE.name " +
                                "FROM spatialEntity innerE, spatialEntity outerE " +
                                "WHERE SDO_INSIDE(outerE.geometry, innerE.geometry) = 'TRUE' " +
                                "AND (innerE.id <> outerE.id) " +
                                "AND innerE.id = ?"
                )) {
            stmt.setInt(1, entity.getId());
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    list.add(rset.getString("name"));
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return list;
    }

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

    private static String geometryToType(EntityGeometry geometry) {
        if (geometry instanceof PointGeometry) {
            return "place";
        } else if (geometry instanceof CircleGeometry) {
            return "largePlace";
        } else if (geometry instanceof RectangleGeometry) {
            return "countryRec";
        } else if (geometry instanceof PolygonGeometry) {
            return "country";
        } else if (geometry instanceof LineGeometry) {
            return "river";
        }
        log.severe("Unknwon geometry.");
        return "unknown";
    }

    private static boolean are_clockwise_points(List<Point> points) {
        // Based on: https://stackoverflow.com/a/1165943/5601069
        int sum = 0;
        for (int i = 0; i < (points.size() - 1); ++i) {
            sum += (points.get(i + 1)
                          .getX() - points.get(i)
                                          .getX()) *
                    (points.get(i + 1)
                           .getY() + points.get(i)
                                           .getY());
        }
        if (points.size() > 1) {
            sum += (points.get(0)
                          .getX() - points.get(points.size() - 1)
                                          .getX()) *
                    (points.get(0)
                           .getY() + points.get(points.size() - 1)
                                           .getY());
        }
        // https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order#comment28629100_1165943
        // DB has 0,0 in left down corner, we have upper left, so this way it works (experimetns).
        log.severe("SUM " + sum);
        return sum > 0;
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

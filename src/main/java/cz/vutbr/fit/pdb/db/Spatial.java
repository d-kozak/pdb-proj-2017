package cz.vutbr.fit.pdb.db;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;
import oracle.spatial.geometry.JGeometry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Log
public class Spatial {

    /**
     * Returns IDs of entities that contain the given point with the given tolerance.
     * @param point
     * @param tolerance
     * @return
     */
    public static ObservableList<Integer> entitiesContainingPoint(Point point, int tolerance) {
        log.info("Searching for near entities...");
        ObservableList<Integer> list = FXCollections.observableArrayList();
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                    "SELECT e.id, e.name " +
                       "FROM spatialEntity e " +
                       "WHERE SDO_RELATE(e.geometry, " +
                       "  SDO_GEOMETRY(2003, NULL, NULL, " +
                       "    SDO_ELEM_INFO_ARRAY(1, 1003, 4), " +
                       "    SDO_ORDINATE_ARRAY(?, ?, ?, ?, ?, ?)), " +
                       "  'mask=anyinteract') = 'TRUE'"
                )) {
            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY() - tolerance);
            stmt.setDouble(3, point.getX());
            stmt.setDouble(4, point.getY() + tolerance);
            stmt.setDouble(5, point.getX() + tolerance);
            stmt.setDouble(6, point.getY());

            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    list.add(rset.getInt("id"));
                    log.info(("Near entities: " + rset.getString("name")));
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

    /**
     * Returns area of the given entity.
     * @param entity
     * @return
     */
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

    /**
     * Returns circumference of the given entity.
     * @param entity
     * @return
     */
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
     * Returns name of the nearest river for the given entity.
     * @param entity
     * @return
     */
    public static String getNearestRiver(Entity entity) {
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "SELECT r.name, SDO_GEOM.SDO_DISTANCE(e.geometry, r.geometry, 0.005) AS dist " +
                                "FROM spatialEntity e, spatialEntity r "+
                                "WHERE e.id = ? AND e.id <> r.id  AND r.entityType = 'river' " +
                                "ORDER BY dist"
                )) {
            stmt.setInt(1, entity.getId());
            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    return rset.getString("name");
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return "";
    }

    /**
     * Edits the given geometry that it does not collide with any polygon in the DB.
     * @param geo
     * @return
     */
    private static JGeometry cutToNotOverlap(JGeometry geo) {
        List<Integer> overlappingIds = new Vector<>();
        Struct geoStruct;
        try {
            geoStruct = JGeometry.storeJS(DBConnection.getInstance().getConnection(), geo);
        } catch (Exception ex) {
            log.severe("To JGeometry: " + ex);
            throw new RuntimeException(ex);
        }
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "SELECT e.id " +
                                "FROM spatialEntity e " +
                                "WHERE SDO_RELATE(e.geometry, ?, 'mask=OVERLAPBDYINTERSECT') = 'TRUE' " +
                                "AND e.entityType IN ('country', 'countryRec')"

                )) {
            stmt.setObject(1, geoStruct);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    overlappingIds.add(rset.getInt("id"));
                }
            } catch (SQLException ex) {
                log.severe("Execute SQL query exception: " + ex);
                throw new RuntimeException(ex);
            }
        } catch (SQLException ex) {
            log.severe("Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return cutWithEntities(geo, overlappingIds);
    }

    /**
     * Edits the given geometry that it does not collide with polygons specified by overlappingIds.
     * @param geo
     * @param overlappingIds
     * @return
     */
    private static JGeometry cutWithEntities(JGeometry geo, List<Integer> overlappingIds) {
        for (Integer id : overlappingIds) {
            Struct geoStruct;
            try {
                geoStruct = JGeometry.storeJS(DBConnection.getInstance().getConnection(), geo);
            } catch (Exception ex) {
                log.severe("To JGeometry: " + ex);
                throw new RuntimeException(ex);
            }
            try (PreparedStatement stmt = DBConnection.getInstance().getConnection()
                    .prepareStatement(
                            "SELECT SDO_GEOM.SDO_DIFFERENCE(?, e.geometry, 0.005) AS geo " +
                                    "FROM spatialEntity e " +
                                    "WHERE e.id = ?"
                    )) {
                stmt.setObject(1, geoStruct);
                stmt.setInt(2, id);
                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        byte[] data = rset.getBytes("geo");
                        geo = JGeometry.load(data);
                    }
                }
            } catch (Exception ex) {
                log.severe("Create SQL statement exception: " + ex);
                throw new RuntimeException(ex);
            }
        }
        return geo;
    }

    /**
     * Returns string representation of the geometry type.
     * @param geometry
     * @return
     */
    public static String geometryToType(EntityGeometry geometry) {
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

    /**
     * Checks whether the given list of points is clockwise or counterclockwise.
     * @param points
     * @return
     */
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
        return sum > 0;
    }

    public static ObservableList<Point> jGeometryToPolygonPoints(JGeometry jGeo) {
        double[] coords = jGeo.getOrdinatesArray();
        int dimensions = jGeo.getDimensions();
        // We do not want the last point - it's same as the first one in the DB
        // and Entity does not store the first one also as the last one
        Integer coordsCount = jGeo.getNumPoints() - 1;
        ObservableList<Point> points = FXCollections.observableArrayList();
        int[] elemInfo = jGeo.getElemInfo();
        if (elemInfo.length > 3) {
            // We want elements only from the first part.
            coordsCount = (elemInfo[3] / dimensions);
        }
        for (Integer i = 0; i < coordsCount * dimensions; i += dimensions) {
            points.add(new Point(coords[i], coords[i + 1]));
        }
        return points;
    }

    /**
     * Transforms the given geometry to JGeometry.
     * @param geometry
     * @return
     */
    static JGeometry geometryToJGeometry(EntityGeometry geometry) {
        if (geometry instanceof PointGeometry) {
            return new JGeometry(
                    ((PointGeometry) geometry).getX(),
                    ((PointGeometry) geometry).getY(),
                    MapMakerDB.SRID
            );
        } else if (geometry instanceof CircleGeometry) {
            CircleGeometry cGeometry = (CircleGeometry) geometry;
            double radius = cGeometry.getRadius();
            return new JGeometry(
                    3,
                    MapMakerDB.SRID,
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
            JGeometry geo = new JGeometry(
                    3,
                    MapMakerDB.SRID,
                    new int[]{1, 1003, 3}, // exterior polygon
                    coords
            );
            return geo;
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
            double coords[] = new double[points.size() * MapMakerDB.DIMENSION];
            for (int i = 0; i < points.size(); i++) {
                coords[i * MapMakerDB.DIMENSION] = points.get(i)
                        .getX();
                coords[i * MapMakerDB.DIMENSION + 1] = points.get(i)
                        .getY();
            }
            ;
            JGeometry geo = new JGeometry(
                    3,
                    MapMakerDB.SRID,
                    new int[]{1, 1003, 1}, // exterior polygon
                    coords
            );
            geo = cutToNotOverlap(geo);
            return geo;
        } else if (geometry instanceof LineGeometry) {
            ObservableList<Point> points = ((LineGeometry) geometry).getPoints();
            double coords[] = new double[points.size() * MapMakerDB.DIMENSION];
            for (int i = 0; i < points.size(); i++) {
                coords[i * MapMakerDB.DIMENSION] = points.get(i)
                        .getX();
                coords[i * MapMakerDB.DIMENSION + 1] = points.get(i)
                        .getY();
            }
            ;
            return new JGeometry(
                    2,
                    MapMakerDB.SRID,
                    new int[]{1, 2, 1},
                    coords
            );
        }
        log.severe("Unknown geometry: " + geometry.getClass());
        return null;
    }
}

package cz.vutbr.fit.pdb.db;

import cz.vutbr.fit.pdb.entity.Entity;
import lombok.extern.java.Log;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Log
public class Description {
    static String loadDescriptionFor(Integer entityId) {
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

    static boolean addDescription(Entity entity) {
        Integer id = DBConnection.getInstance()
                .getMaxId("description") + 1;
        try (PreparedStatement stmt = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(
                        "INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) " +
                           "VALUES(?, ?, ?, ?, ?)"
                )) {
            stmt.setInt(1, id);
            stmt.setString(2, entity.getDescription());
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            stmt.setInt(5, entity.getId());
            log.severe("UPDATING DESCR: " + entity.getDescription());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.severe("Update description: Create SQL statement exception: " + ex);
            throw new RuntimeException(ex);
        }
        return true;
    }
}

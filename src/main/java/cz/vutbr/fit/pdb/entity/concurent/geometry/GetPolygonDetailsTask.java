package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.db.Picture;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.PolygonDetails;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetPolygonDetailsTask extends Task<PolygonDetails> {
    private Entity entity;

    @Override
    protected PolygonDetails call() throws Exception {
        PolygonDetails polygonDetails = new PolygonDetails();
        polygonDetails.setArea(MapMakerDB.getArea(entity));
        polygonDetails.setCircumference(MapMakerDB.getCircumference(entity));
        polygonDetails.setEntitiesInside(MapMakerDB.entitiesInside(entity));
        return polygonDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

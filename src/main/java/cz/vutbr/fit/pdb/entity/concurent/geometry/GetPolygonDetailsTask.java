package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.PolygonDetails;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetPolygonDetailsTask extends Task<PolygonDetails> {
    private Entity entity;

    @Override
    protected PolygonDetails call() throws Exception {
        PolygonDetails polygonDetails = new PolygonDetails();
        polygonDetails.setArea(Spatial.getArea(entity));
        polygonDetails.setCircumference(Spatial.getCircumference(entity));
        polygonDetails.setEntitiesInside(Spatial.entitiesInside(entity));
        return polygonDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.CircleDetails;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetCircleDetailsTask extends Task<CircleDetails> {
    private Entity entity;

    @Override
    protected CircleDetails call() throws Exception {
        CircleDetails circleDetails = new CircleDetails();
        circleDetails.setArea(Spatial.getArea(entity));
        circleDetails.setCircumference(Spatial.getCircumference(entity));
        circleDetails.setEntitiesInside(Spatial.entitiesInside(entity));
        circleDetails.setNearestRiver(Spatial.getNearestRiver(entity));
        return circleDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

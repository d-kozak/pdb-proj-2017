package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.CircleDetails;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetCircleDetailsTask extends Task<CircleDetails> {
    private Entity entity;

    @Override
    protected CircleDetails call() throws Exception {
        CircleDetails circleDetails = new CircleDetails();
        circleDetails.setArea(MapMakerDB.getArea(entity));
        circleDetails.setCircumference(MapMakerDB.getCircumference(entity));
        circleDetails.setEntitiesInside(MapMakerDB.entitiesInside(entity));
        return circleDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

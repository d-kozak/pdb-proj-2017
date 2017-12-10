package cz.vutbr.fit.pdb.entity.concurent.geometry;

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
        log.severe("Not impl yet");
        PolygonDetails polygonDetails = new PolygonDetails();
        polygonDetails.setArea(42);
        polygonDetails.setCircumference(42);
        polygonDetails.setEntitiesInside(FXCollections.observableArrayList("Panem, Narnie, Severni Korea"));
        Thread.sleep(2000);
        return polygonDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

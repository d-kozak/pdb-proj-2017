package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.PointDetails;
import javafx.concurrent.Task;


public class GetPointDetailsTask extends Task<PointDetails> {
    private Entity entity;

    @Override
    protected PointDetails call() throws Exception {
        PointDetails pointDetails = new PointDetails();
        pointDetails.setNearestRiver(Spatial.getNearestRiver(entity));
        return pointDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

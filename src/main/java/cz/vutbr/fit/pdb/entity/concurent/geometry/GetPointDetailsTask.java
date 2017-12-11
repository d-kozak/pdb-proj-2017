package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.PointDetails;
import javafx.concurrent.Task;


public class GetPointDetailsTask extends Task<PointDetails> {
    private Entity entity;

    @Override
    protected PointDetails call() throws Exception {
        Thread.sleep(2000);
        PointDetails pointDetails = new PointDetails();
        pointDetails.setNearestRiver("Amazonka");
        return pointDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

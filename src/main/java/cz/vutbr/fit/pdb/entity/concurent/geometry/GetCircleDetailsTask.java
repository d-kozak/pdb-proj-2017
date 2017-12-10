package cz.vutbr.fit.pdb.entity.concurent.geometry;

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
        log.severe("Not impl yet!");
        CircleDetails circleDetails = new CircleDetails();
        circleDetails.setArea(42);
        circleDetails.setCircumference(42);
        circleDetails.setEntitiesInside(FXCollections.observableArrayList("Udoli", "Sever", "Dorn", "Vysoka Zahrada"));
        Thread.sleep(2000);
        return circleDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

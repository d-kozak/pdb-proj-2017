package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.geometry.LineDetails;
import javafx.concurrent.Task;
import lombok.extern.java.Log;


@Log
public class GetLineDetailsTask extends Task<LineDetails> {
    private Entity entity;

    @Override
    protected LineDetails call() throws Exception {
        LineDetails lineDetails = new LineDetails();
        lineDetails.setLength(Spatial.getCircumference(entity));
        return lineDetails;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

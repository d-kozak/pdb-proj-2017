package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.startWithTimeout;

@Log
public class AddEntityTask extends Task<Entity> {

    private Entity entity;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected Entity call() throws Exception {
        startWithTimeout(3000, this);
        return MapMakerDB.insertEntity(entity);
    }

}

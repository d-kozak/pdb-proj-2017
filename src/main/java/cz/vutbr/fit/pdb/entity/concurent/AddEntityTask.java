package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class AddEntityTask extends Task<Void> {

    private Entity entity;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected Void call() throws Exception {
        MapMakerDB.insertEntity(entity);
        return null;
    }

}

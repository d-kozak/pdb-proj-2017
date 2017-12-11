package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class UpdateEntityTask extends Task<Entity> {
    private Entity entity;
    private String field;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setFieldName(String field) {
        this.field = field;
    }

    @Override
    protected Entity call() throws Exception {
        JavaFXUtils.startWithTimeout(3000, this);
        return MapMakerDB.updateEntity(entity, field);
    }
}

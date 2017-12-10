package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class UpdateEntityTask extends Task<Void> {
    private Entity entity;
    private String field;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setFieldName(String field) {
        this.field = field;
    }

    @Override
    protected Void call() throws Exception {
        MapMakerDB.updateEntity(entity, field);
        return null;
    }
}

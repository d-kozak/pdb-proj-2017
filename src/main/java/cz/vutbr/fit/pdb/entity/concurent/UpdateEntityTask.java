package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.utils.ExceptionGun;
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
        log.severe("Not impl yet!");
        ExceptionGun.throwMeMaybe();
        return null;
    }
}

package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.utils.DummyData;
import cz.vutbr.fit.pdb.utils.ExceptionGun;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class LoadAllEntitiesTask extends Task<ObservableList<Entity>> {


    @Override
    protected ObservableList<Entity> call() throws Exception {
        return MapMakerDB.getEntities();
    }
}

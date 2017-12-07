package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.Entity;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

import java.util.Comparator;
import java.util.function.ToIntFunction;

@Log
public class LoadAllEntitiesTask extends Task<ObservableList<Entity>> {


    @Override
    protected ObservableList<Entity> call() throws Exception {
        ObservableList<Entity> entities = MapMakerDB.getEntities();
        entities.sort(Comparator.comparingInt(entityToInt())
                                .reversed());
        return entities;
    }

    private ToIntFunction<Entity> entityToInt() {
        return entity -> entity.getGeometryType()
                               .ordinal();
    }
}

package cz.vutbr.fit.pdb.entity.concurent;

import cz.vutbr.fit.pdb.db.MapMakerDB;
import javafx.concurrent.Task;

public class InitDatabaseTask extends Task<Void> {
    @Override
    protected Void call() throws Exception {
        MapMakerDB db = MapMakerDB.getInstance();
        if (!db.initDB("init_db.sql")) {
            throw new RuntimeException("Failed");
        }
        return null;
    }
}

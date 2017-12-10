package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class RemovePictureTask extends Task<Void> {
    private EntityImage entityImage;

    @Override
    protected Void call() throws Exception {
        log.severe("Not impl yet!");
        return null;
    }

    public EntityImage getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }
}

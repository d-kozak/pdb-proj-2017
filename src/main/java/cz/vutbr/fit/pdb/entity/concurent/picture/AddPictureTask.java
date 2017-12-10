package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class AddPictureTask extends Task<Void> {
    private EntityImage entityImage;
    private int entityId;

    @Override
    protected Void call() throws Exception {
        log.severe("Not impl yet");
        return null;
    }


    public EntityImage getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}

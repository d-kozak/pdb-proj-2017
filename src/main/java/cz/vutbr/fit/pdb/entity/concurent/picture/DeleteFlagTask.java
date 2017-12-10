package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.db.Picture;
import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class DeleteFlagTask extends Task<Void> {
    private EntityImage entityImage;

    @Override
    protected Void call() throws Exception {
        Picture.deleteFlag(entityImage);
        return null;
    }

    public EntityImage getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }
}

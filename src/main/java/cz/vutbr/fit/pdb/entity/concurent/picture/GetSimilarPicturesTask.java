package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.db.Picture;
import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetSimilarPicturesTask extends Task<ObservableList<EntityImage>> {
    private EntityImage entityImage;
    private static final Integer COUNT = 2;

    @Override
    protected ObservableList<EntityImage> call() throws Exception {
        return Picture.findSimilar(entityImage, COUNT);
    }

    public EntityImage getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }
}

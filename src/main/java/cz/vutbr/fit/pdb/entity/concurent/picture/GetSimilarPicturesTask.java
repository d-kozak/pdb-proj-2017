package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class GetSimilarPicturesTask extends Task<ObservableList<EntityImage>> {
    private EntityImage entityImage;

    @Override
    protected ObservableList<EntityImage> call() throws Exception {
        log.severe("Not impl yet!");
        return FXCollections.observableArrayList();
    }

    public EntityImage getEntityImage() {
        return entityImage;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }
}

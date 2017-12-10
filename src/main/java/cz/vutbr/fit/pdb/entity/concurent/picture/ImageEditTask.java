package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class ImageEditTask extends Task<EntityImage> {
    private ImageOperation imageOperation;
    private EntityImage entityImage;

    @Override
    protected EntityImage call() throws Exception {
        log.severe("Not impl yet");
        return null;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }

    public void setImageOperation(ImageOperation imageOperation) {
        this.imageOperation = imageOperation;
    }
}

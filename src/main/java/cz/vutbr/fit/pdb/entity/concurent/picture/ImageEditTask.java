package cz.vutbr.fit.pdb.entity.concurent.picture;

import cz.vutbr.fit.pdb.db.Picture;
import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.concurrent.Task;
import lombok.extern.java.Log;

@Log
public class ImageEditTask extends Task<EntityImage> {
    private ImageOperation imageOperation;
    private EntityImage entityImage;

    @Override
    protected EntityImage call() throws Exception {
        switch (imageOperation) {
            case GREYSCALE:
                Picture.makeImageGrayscale(entityImage);
                break;
            case MONOCHROMATIC:
                break;
            case ROTATE_LEFT:
                break;
            case ROTATE_RIGHT:
                break;
        }
        return null;
    }

    public void setEntityImage(EntityImage entityImage) {
        this.entityImage = entityImage;
    }

    public void setImageOperation(ImageOperation imageOperation) {
        this.imageOperation = imageOperation;
    }
}

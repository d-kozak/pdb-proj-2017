package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.concurent.picture.ImageOperation;
import javafx.scene.control.ListCell;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PictureListViewCell extends ListCell<EntityImage> {

    private Consumer<EntityImage> onDelete;
    private Consumer<EntityImage> setAsFlag;
    private BiConsumer<EntityImage, ImageOperation> onImageOperation;

    public PictureListViewCell(Consumer<EntityImage> onDelete, Consumer<EntityImage> setAsFlag, BiConsumer<EntityImage, ImageOperation> onImageOperation) {
        this.onDelete = onDelete;
        this.setAsFlag = setAsFlag;
        this.onImageOperation = onImageOperation;
    }

    @Override
    protected void updateItem(EntityImage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            PictureListViewItem pictureListViewItem = new PictureListViewItem(item, onDelete, setAsFlag, onImageOperation);
            setGraphic(pictureListViewItem.getView());
        }
    }
}

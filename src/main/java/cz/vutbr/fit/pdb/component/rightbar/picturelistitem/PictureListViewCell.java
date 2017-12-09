package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.scene.control.ListCell;

import java.util.function.Consumer;

public class PictureListViewCell extends ListCell<EntityImage> {

    private Consumer<EntityImage> onDelete;
    private Consumer<EntityImage> setAsFlag;

    public PictureListViewCell(Consumer<EntityImage> onDelete, Consumer<EntityImage> setAsFlag) {
        this.onDelete = onDelete;
        this.setAsFlag = setAsFlag;
    }

    @Override
    protected void updateItem(EntityImage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            PictureListViewItem pictureListViewItem = new PictureListViewItem(item, onDelete, setAsFlag);
            setGraphic(pictureListViewItem.getView());
        }
    }
}

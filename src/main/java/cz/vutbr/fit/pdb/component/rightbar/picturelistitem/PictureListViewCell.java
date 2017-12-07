package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;

import java.util.function.Consumer;

public class PictureListViewCell extends ListCell<EntityImage> {

    private ObservableList<EntityImage> images;
    private Consumer<EntityImage> setAsFlag;

    public PictureListViewCell(ObservableList<EntityImage> images, Consumer<EntityImage> setAsFlag) {
        this.images = images;
        this.setAsFlag = setAsFlag;
    }

    @Override
    protected void updateItem(EntityImage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            PictureListViewItem pictureListViewItem = new PictureListViewItem(item, images, setAsFlag);
            setGraphic(pictureListViewItem.getView());
        }
    }
}

package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class PictureListViewCell extends ListCell<Image> {

    private ObservableList<Image> images;
    private Consumer<Image> setAsFlag;

    public PictureListViewCell(ObservableList<Image> images, Consumer<Image> setAsFlag) {
        this.images = images;
        this.setAsFlag = setAsFlag;
    }

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            PictureListViewItem pictureListViewItem = new PictureListViewItem(item, images, setAsFlag);
            setGraphic(pictureListViewItem.getView());
        }
    }
}

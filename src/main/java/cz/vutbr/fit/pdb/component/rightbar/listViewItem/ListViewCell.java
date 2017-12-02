package cz.vutbr.fit.pdb.component.rightbar.listViewItem;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class ListViewCell extends ListCell<Image> {

    private ObservableList<Image> images;
    private Consumer<Image> setAsFlag;

    public ListViewCell(ObservableList<Image> images, Consumer<Image> setAsFlag) {
        this.images = images;
        this.setAsFlag = setAsFlag;
    }

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            ListItem listItem = new ListItem(item, images, setAsFlag);
            setGraphic(listItem.getHbox());
        }
    }
}

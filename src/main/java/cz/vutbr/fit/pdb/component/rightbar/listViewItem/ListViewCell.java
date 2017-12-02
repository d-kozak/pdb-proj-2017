package cz.vutbr.fit.pdb.component.rightbar.listViewItem;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

public class ListViewCell extends ListCell<Image> {

    private ObservableList<Image> images;

    public ListViewCell(ObservableList<Image> images) {
        this.images = images;
    }

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            ListItem listItem = new ListItem(item, images);
            setGraphic(listItem.getHbox());
        }
    }
}

package cz.vutbr.fit.pdb.component.rightbar.listViewItem;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;

public class ListViewCell extends ListCell<Image> {

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            ListItem listItem = new ListItem();
            listItem.setImage(item);
            setGraphic(listItem.getHbox());
        }
    }
}

package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.scene.control.ListCell;

public class PointListViewCell extends ListCell<Point> {

    @Override
    protected void updateItem(Point point, boolean empty) {
        super.updateItem(point, empty);
        if (point != null) {
            PointListViewItem pointListViewItem = new PointListViewItem(point);
            setGraphic(pointListViewItem.getView());
        }
    }
}


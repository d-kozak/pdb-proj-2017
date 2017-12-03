package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.scene.control.ListCell;

import java.util.function.Consumer;

public class PointListViewCell extends ListCell<Point> {

    private Consumer<Point> onDelete;
    private Configuration configuration;


    public PointListViewCell(Configuration configuration, Consumer<Point> onDelete) {
        this.configuration = configuration;
        this.onDelete = onDelete;
    }

    @Override
    protected void updateItem(Point point, boolean empty) {
        super.updateItem(point, empty);
        if (point != null) {
            PointListViewItem pointListViewItem = new PointListViewItem(point, onDelete, configuration);
            setGraphic(pointListViewItem.getView());
        }
    }
}


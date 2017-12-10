package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.scene.control.ListCell;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PointListViewCell extends ListCell<Point> {

    private BiConsumer<Point, Point> onUpdate;
    private Consumer<Point> onDelete;
    private Configuration configuration;


    public PointListViewCell(Configuration configuration, BiConsumer<Point, Point> onUpdate, Consumer<Point> onDelete) {
        this.configuration = configuration;
        this.onUpdate = onUpdate;
        this.onDelete = onDelete;
    }

    @Override
    protected void updateItem(Point point, boolean empty) {
        super.updateItem(point, empty);
        if (point != null) {
            PointListViewItem pointListViewItem = new PointListViewItem(point, onUpdate, onDelete, configuration);
            setGraphic(pointListViewItem.getView());
        } else setGraphic(null);
    }
}


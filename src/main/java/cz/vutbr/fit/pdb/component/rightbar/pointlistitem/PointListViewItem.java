package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PointListViewItem {

    @FXML
    private HBox hbox;

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    public PointListViewItem(Point point, BiConsumer<Point, Point> onUpdate, Consumer<Point> onDelete, Configuration configuration) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pointlistitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        xField.setText(point.getX() + "");
        yField.setText(point.getY() + "");

        javafx.event.EventHandler<ActionEvent> updateHandler = event -> {
            Optional<Point> newPointOptional = Point.of(xField.getText(), yField.getText());
            newPointOptional.ifPresent(newPoint -> {
                onUpdate.accept(point, newPoint);
            });
        };
        xField.setOnAction(updateHandler);
        yField.setOnAction(updateHandler);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deletePoint = new MenuItem("Delete point");
        deletePoint.setOnAction(event -> {
            onDelete.accept(point);
            configuration.getMapRenderer()
                         .redraw();
        });

        contextMenu.getItems()
                   .addAll(deletePoint);
        hbox.setOnContextMenuRequested(event -> {
            contextMenu.show(getView(), event.getSceneX(), event.getSceneY());
        });
    }

    public HBox getView() {
        return hbox;
    }
}
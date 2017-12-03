package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class PointListViewItem {

    @FXML
    private HBox hbox;

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    public PointListViewItem(Point point, Consumer<Point> onDelete, Configuration configuration) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pointlistitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        xField.textProperty()
              .bindBidirectional(point.xProperty(), new StringNumConverter());
        xField.textProperty()
              .addListener((observable, oldValue, newValue) -> {
                  configuration.getMapRenderer()
                               .redraw();
              });
        yField.textProperty()
              .bindBidirectional(point.yProperty(), new StringNumConverter());
        yField.textProperty()
              .addListener((observable, oldValue, newValue) -> {
                  configuration.getMapRenderer()
                               .redraw();
              });

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
package cz.vutbr.fit.pdb.component.rightbar.pointlistitem;

import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PointListViewItem {

    @FXML
    private HBox hbox;

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    public PointListViewItem(Point point) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pointlistitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        xField.textProperty()
              .bindBidirectional(point.xProperty(), new StringNumConverter());
        yField.textProperty()
              .bindBidirectional(point.yProperty(), new StringNumConverter());

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deletePoint = new MenuItem("Delete point");

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
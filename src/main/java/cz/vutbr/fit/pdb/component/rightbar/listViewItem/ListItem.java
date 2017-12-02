package cz.vutbr.fit.pdb.component.rightbar.listViewItem;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class ListItem {
    @FXML
    private ImageView imageView;

    @FXML
    private HBox hbox;

    public ListItem(Image image, ObservableList<Image> allImages, Consumer<Image> setAsFlag) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("listitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageView.imageProperty()
                 .setValue(image);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deletePictureMenuItem = new MenuItem("Delete picture");
        deletePictureMenuItem.setOnAction(event -> {
            allImages.remove(image);
        });
        MenuItem setAsFlagMenuItem = new MenuItem("Set picture as flag");
        setAsFlagMenuItem.setOnAction(event -> {
            setAsFlag.accept(image);
        });
        contextMenu.getItems()
                   .addAll(setAsFlagMenuItem, deletePictureMenuItem);
        hbox.setOnContextMenuRequested(event -> {
            contextMenu.show(getHbox(), event.getSceneX(), event.getSceneY());
        });
    }

    public HBox getHbox() {
        return hbox;
    }
}

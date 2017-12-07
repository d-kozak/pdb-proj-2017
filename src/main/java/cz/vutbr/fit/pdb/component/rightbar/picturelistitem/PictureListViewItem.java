package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import cz.vutbr.fit.pdb.entity.EntityImage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class PictureListViewItem {
    @FXML
    private ImageView imageView;

    @FXML
    private HBox hbox;

    public PictureListViewItem(EntityImage image, ObservableList<EntityImage> allImages, Consumer<EntityImage> setAsFlag) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("picturelistitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageView.imageProperty()
                 .setValue(image.getImage());
        Tooltip.install(imageView, new Tooltip(image.getDescription()));
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
            contextMenu.show(getView(), event.getSceneX(), event.getSceneY());
        });
    }

    public HBox getView() {
        return hbox;
    }
}

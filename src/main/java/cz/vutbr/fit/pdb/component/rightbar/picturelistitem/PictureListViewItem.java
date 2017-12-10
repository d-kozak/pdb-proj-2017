package cz.vutbr.fit.pdb.component.rightbar.picturelistitem;

import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.concurent.picture.ImageOperation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PictureListViewItem {
    @FXML
    private ImageView imageView;

    @FXML
    private HBox hbox;

    public PictureListViewItem(EntityImage image, Consumer<EntityImage> onDelete, Consumer<EntityImage> setAsFlag, BiConsumer<EntityImage, ImageOperation> onImageOperation) {
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
            onDelete.accept(image);
        });
        MenuItem setAsFlagMenuItem = new MenuItem("Set picture as flag");
        setAsFlagMenuItem.setOnAction(event -> {
            setAsFlag.accept(image);
        });
        MenuItem rotateLeftMenuItem = new MenuItem("Rotate left");
        rotateLeftMenuItem.setOnAction(event -> {
            onImageOperation.accept(image, ImageOperation.ROTATE_LEFT);
        });

        MenuItem rotateRightMenuItem = new MenuItem("Rotate right");
        rotateRightMenuItem.setOnAction(event -> {
            onImageOperation.accept(image, ImageOperation.ROTATE_RIGHT);
        });

        MenuItem monochromaticMenuItem = new MenuItem("Monochromatic");
        monochromaticMenuItem.setOnAction(event -> {
            onImageOperation.accept(image, ImageOperation.MONOCHROMATIC);
        });

        MenuItem greyscaleMenuItem = new MenuItem("Greyscale");
        greyscaleMenuItem.setOnAction(event -> {
            onImageOperation.accept(image, ImageOperation.GREYSCALE);
        });

        contextMenu.getItems()
                   .addAll(setAsFlagMenuItem, deletePictureMenuItem, rotateLeftMenuItem, rotateRightMenuItem, monochromaticMenuItem, greyscaleMenuItem);
        hbox.setOnContextMenuRequested(event -> {
            contextMenu.show(getView(), event.getSceneX(), event.getSceneY());
        });
    }

    public HBox getView() {
        return hbox;
    }
}

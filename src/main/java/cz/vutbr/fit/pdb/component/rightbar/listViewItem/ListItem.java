package cz.vutbr.fit.pdb.component.rightbar.listViewItem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ListItem {
    @FXML
    private ImageView imageView;

    @FXML
    private HBox hbox;

    public ListItem() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("listitem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setImage(Image image) {
        imageView.imageProperty()
                 .set(image);
    }

    public HBox getHbox() {
        return hbox;
    }
}

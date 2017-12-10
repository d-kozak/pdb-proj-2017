package cz.vutbr.fit.pdb.component.loadpicture;

import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

@Log
public class LoadPicturePresenter implements Initializable {
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView imageView;
    @FXML
    private TextArea description;

    private StringProperty descriptionProperty = new SimpleStringProperty();
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>(LocalDate.now());

    private Stage stage;
    private EntityImage result = new EntityImage();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.imageProperty()
                 .bindBidirectional(imageProperty);
        description.textProperty()
                   .bindBidirectional(descriptionProperty);
        datePicker.valueProperty()
                  .bindBidirectional(dateProperty);
    }

    @FXML
    private void onSave(ActionEvent event) {
        result.setDescription(descriptionProperty.get());
        result.setImage(imageProperty.get());
        result.setTime(dateProperty.get());
        log.info("Created image : " + result);
        JavaFXUtils.closeWindow(event);
    }

    @FXML
    private void onCancel(ActionEvent event) {
        JavaFXUtils.closeWindow(event);
    }

    @FXML
    private void loadImage(MouseEvent mouseEvent) {
        val fileChooser = new FileChooser();
        fileChooser.setTitle("Select new picture");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            val image = new Image(file.toURI()
                                      .toString());
            imageView.setImage(image);
            try {
                result.setUrl(file.toURL()
                                  .toExternalForm());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            log.info("Image " + file.getName() + " loaded successfully");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public Optional<EntityImage> getResult() {
        return Optional.ofNullable(result);
    }
}

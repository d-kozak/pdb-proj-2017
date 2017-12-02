package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.component.rightbar.listViewItem.ListViewCell;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.val;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RightbarPresenter implements Initializable {
    @FXML
    private Accordion accordion;

    @FXML
    private TitledPane commonPane;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField nameField;
    @FXML
    private ImageView flagView;
    @FXML
    private TabPane geometryTabPane;

    @FXML
    private Tab pointTab;
    @FXML
    private Tab lineTab;
    @FXML
    private Tab circleTab;
    @FXML
    private Tab polygonTab;

    @FXML
    private ListView<Image> picturesView;

    @Inject
    private EntityService entityService;

    @Inject
    private Stage mainStage;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initViewForEntity(selectedEntityService.getObjectProperty());
        commonPane.setExpanded(true);
        accordion.setExpandedPane(commonPane);
    }

    private void initViewForEntity(Entity entity) {
        nameField.textProperty()
                 .bindBidirectional(entity.nameProperty());
        descriptionField.textProperty()
                        .bindBidirectional(entity.descriptionProperty());
        flagView.imageProperty()
                .setValue(entity.getFlag());
        picturesView.setItems(entity.getImages());
        picturesView.setCellFactory(param -> new ListViewCell());
    }

    @FXML
    private void onLoadNewPhoto(ActionEvent event) {
        val fileChooser = new FileChooser();
        fileChooser.setTitle("Select new picture");
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            val image = new Image(file.toURI()
                                      .toString());
            selectedEntityService.getObjectProperty()
                                 .getImages()
                                 .add(image);

        }
    }
}

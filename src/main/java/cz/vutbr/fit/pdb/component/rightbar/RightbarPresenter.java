package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.service.EntityService;
import cz.vutbr.fit.pdb.service.SelectedEntityService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
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
    private ListView picturesView;

    @Inject
    private EntityService entityService;

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
    }

    @FXML
    private void onLoadNewPhoto(ActionEvent event) {

    }
}

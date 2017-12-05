package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.component.rightbar.geometry.circleinfo.CircleInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo.LineInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo.PointInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo.PolygonInfoView;
import cz.vutbr.fit.pdb.component.rightbar.picturelistitem.PictureListViewCell;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class RightbarPresenter implements Initializable {

    @FXML
    private TitledPane geometryTitledPane;

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
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    @FXML
    private ListView<Image> picturesView;

    @Inject
    private EntityService entityService;

    @Inject
    private Stage mainStage;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private Configuration configuration;

    private Entity selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selectedEntity = selectedEntityService.getEntityProperty();
        if (selectedEntity != null)
            initViewForEntity(selectedEntity);
        selectedEntityService.entityPropertyProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 if (newValue != null) {
                                     clearView();
                                     initViewForEntity(newValue);
                                 } else {
                                     clearView();
                                 }
                             });
        commonPane.setExpanded(true);
        accordion.setExpandedPane(commonPane);
    }

    private void clearView() {
        unbindAll();
        nameField.textProperty()
                 .setValue("");
        descriptionField.textProperty()
                        .setValue("");
        flagView.imageProperty()
                .setValue(null);
        picturesView.setItems(FXCollections.observableArrayList());
        fromDate.getEditor()
                .clear();
        toDate.getEditor()
              .clear();
        geometryTitledPane.setContent(null);
        selectedEntity = null;
    }

    private void initViewForEntity(Entity entity) {
        log.info("displaying entity: " + entity);
        this.selectedEntity = entity;
        nameField.textProperty()
                 .bindBidirectional(entity.nameProperty());
        descriptionField.textProperty()
                        .bindBidirectional(entity.descriptionProperty());
        flagView.imageProperty()
                .setValue(entity.getFlag());
        picturesView.setItems(entity.getImages());
        picturesView.setCellFactory(param -> new PictureListViewCell(entity.getImages(), image -> {
            flagView.imageProperty()
                    .setValue(image);
            entity.flagProperty()
                  .setValue(image);
        }));
        fromDate.valueProperty()
                .bindBidirectional(entity.fromProperty());
        toDate.valueProperty()
              .bindBidirectional(entity.toProperty());

        switch (entity.getGeometryType()) {
            case POINT:
                initForPoint();
                break;
            case LINE:
                initForLine();
                break;
            case CIRCLE:
                initForCircle();
                break;
            case POLYGON:
                initForPolygon();
                break;

            default:
                throw new RuntimeException();
        }
    }

    private void unbindAll() {
        if (selectedEntity == null) {
            log.severe("Selected entity is null, cannot unbind");
            return;
        }
        nameField.textProperty()
                 .unbindBidirectional(selectedEntity.nameProperty());
        descriptionField.textProperty()
                        .unbindBidirectional(selectedEntity.descriptionProperty());
        flagView.imageProperty()
                .unbindBidirectional(selectedEntity.flagProperty());
        fromDate.valueProperty()
                .unbindBidirectional(selectedEntity.fromProperty());
        toDate.valueProperty()
              .unbindBidirectional(selectedEntity.toProperty());
    }

    private void initForPoint() {
        geometryTitledPane.setContent(new PointInfoView().getView());
    }

    private void initForLine() {
        geometryTitledPane.setContent(new LineInfoView().getView());
    }

    private void initForCircle() {
        geometryTitledPane.setContent(new CircleInfoView().getView());
    }

    private void initForPolygon() {
        geometryTitledPane.setContent(new PolygonInfoView().getView());
    }

    @FXML
    private void onLoadNewPhoto(ActionEvent event) {
        val fileChooser = new FileChooser();
        fileChooser.setTitle("Select new picture");
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            val image = new Image(file.toURI()
                                      .toString());
            selectedEntityService.getEntityProperty()
                                 .getImages()
                                 .add(image);
            log.info("Image " + file.getName() + " loaded successfully");
        }
    }
}

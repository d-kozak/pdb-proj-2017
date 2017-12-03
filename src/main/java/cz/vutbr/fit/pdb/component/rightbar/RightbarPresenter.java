package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.component.rightbar.listViewItem.ListViewCell;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.beans.property.DoubleProperty;
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

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;

    @FXML
    private TextField startXField;

    @FXML
    private TextField startYField;

    @FXML
    private TextField endXField;

    @FXML
    private TextField endYField;

    @FXML
    private TextField centerXField;

    @FXML
    private TextField centerYField;

    @FXML
    private TextField radiusField;

    @Inject
    private EntityService entityService;

    @Inject
    private Stage mainStage;

    @Inject
    private SelectedEntityService selectedEntityService;

    private Entity selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selectedEntity = selectedEntityService.getEntityProperty();
        initViewForEntity(selectedEntity);
        selectedEntityService.entityPropertyProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 if (newValue != null) {
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
        fromDate.getEditor()
                .clear();
        toDate.getEditor()
              .clear();
        selectedEntity = null;
    }

    private void initViewForEntity(Entity entity) {
        log.info("displaying entity: " + entity);
        unbindAll();
        nameField.textProperty()
                 .bindBidirectional(entity.nameProperty());
        descriptionField.textProperty()
                        .bindBidirectional(entity.descriptionProperty());
        flagView.imageProperty()
                .setValue(entity.getFlag());
        picturesView.setItems(entity.getImages());
        picturesView.setCellFactory(param -> new ListViewCell(entity.getImages(), image -> {
            flagView.imageProperty()
                    .setValue(image);
        }));
        fromDate.valueProperty()
                .bindBidirectional(entity.fromProperty());
        toDate.valueProperty()
              .bindBidirectional(entity.toProperty());

        switch (entity.getGeometryType()) {
            case POINT:
                initForPoint(entity.getGeometry());
                break;
            case LINE:
                initForLine(entity.getGeometry());
                break;
            case CIRCLE:
                initForCircle(entity.getGeometry());
                break;
            case POLYGON:
                initForPolygon(entity.getGeometry());
                break;

            default:
                throw new RuntimeException();
        }
        selectedEntity = entity;
    }

    private void unbindAll() {
        if (selectedEntity == null)
            return;
        nameField.textProperty()
                 .unbindBidirectional(selectedEntity.nameProperty());
        descriptionField.textProperty()
                        .unbindBidirectional(selectedEntity.descriptionProperty());
        flagView.imageProperty()
                .unbindBidirectional(selectedEntity.flagProperty());
        picturesView.setItems(FXCollections.observableArrayList());
        fromDate.valueProperty()
                .unbindBidirectional(selectedEntity.fromProperty());
        toDate.valueProperty()
              .unbindBidirectional(selectedEntity.toProperty());

        switch (selectedEntity.getGeometryType()) {
            case POINT:
                unbindPoint(selectedEntity.getGeometry());
                break;
            case LINE:
                unbindLine(selectedEntity.getGeometry());
                break;
            case CIRCLE:
                unbindCircle(selectedEntity.getGeometry());
                break;
            case POLYGON:
                unbindPolygon(selectedEntity.getGeometry());
                break;

            default:
                throw new RuntimeException();
        }
    }

    private void unbindPolygon(EntityGeometry geometry) {
        throw new RuntimeException("Not impl yet");
    }

    private void unbindCircle(EntityGeometry geometry) {
        Object[] description = ((Object[]) geometry.getDescription());
        Point center = ((Point) description[0]);
        DoubleProperty radius = ((DoubleProperty) description[1]);
        centerXField.textProperty()
                    .unbindBidirectional(center.xProperty());
        centerYField.textProperty()
                    .unbindBidirectional(center.yProperty());
        radiusField.textProperty()
                   .unbindBidirectional(radius);
    }

    private void unbindLine(EntityGeometry geometry) {
        Point[] points = ((Point[]) geometry.getDescription());
        Point start = points[0];
        Point end = points[1];
        startXField.textProperty()
                   .unbindBidirectional(start.xProperty());
        startYField.textProperty()
                   .unbindBidirectional(start.yProperty());
        endXField.textProperty()
                 .unbindBidirectional(end.xProperty());
        endYField.textProperty()
                 .unbindBidirectional(end.yProperty());
    }

    private void unbindPoint(EntityGeometry geometry) {
        Point description = ((Point) geometry.getDescription());
        xField.textProperty()
              .unbindBidirectional(description.xProperty());
        yField.textProperty()
              .unbindBidirectional(description.yProperty());
    }

    private void initForPolygon(EntityGeometry geometry) {
        selectGeometryTab(polygonTab);
        throw new RuntimeException("Not impl yet");
    }

    private void initForCircle(EntityGeometry geometry) {
        selectGeometryTab(circleTab);
        Object[] description = (Object[]) geometry.getDescription();
        Point center = ((Point) description[0]);
        DoubleProperty radius = ((DoubleProperty) description[1]);
        centerXField.textProperty()
                    .bindBidirectional(center.xProperty(), new StringNumConverter());
        centerYField.textProperty()
                    .bindBidirectional(center.yProperty(), new StringNumConverter());
        radiusField.textProperty()
                   .bindBidirectional(radius, new StringNumConverter());
    }

    private void initForLine(EntityGeometry geometry) {
        selectGeometryTab(lineTab);
        Point[] points = (Point[]) geometry.getDescription();
        Point start = points[0];
        Point end = points[1];
        startXField.textProperty()
                   .bindBidirectional(start.xProperty(), new StringNumConverter());
        startYField.textProperty()
                   .bindBidirectional(start.yProperty(), new StringNumConverter());
        endXField.textProperty()
                 .bindBidirectional(end.xProperty(), new StringNumConverter());
        endYField.textProperty()
                 .bindBidirectional(end.yProperty(), new StringNumConverter());
    }

    private void initForPoint(EntityGeometry geometry) {
        selectGeometryTab(pointTab);
        Point description = (Point) geometry.getDescription();
        xField.textProperty()
              .bindBidirectional(description.xProperty(), new StringNumConverter());
        yField.textProperty()
              .bindBidirectional(description.yProperty(), new StringNumConverter());
    }

    private void selectGeometryTab(Tab tab) {
        geometryTabPane.getTabs()
                       .clear();
        geometryTabPane.getTabs()
                       .add(tab);
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

        }
    }
}

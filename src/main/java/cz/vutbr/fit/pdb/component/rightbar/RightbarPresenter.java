package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.component.loadpicture.LoadPicturePresenter;
import cz.vutbr.fit.pdb.component.loadpicture.LoadPictureView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.circleinfo.CircleInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo.LineInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo.PointInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo.PolygonInfoView;
import cz.vutbr.fit.pdb.component.rightbar.picturelistitem.PictureListViewCell;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
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
    private ListView<EntityImage> picturesView;

    @Inject
    private EntityService entityService;

    @Inject
    private Stage mainStage;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private Configuration configuration;

    private Entity selectedEntity;
    private ChangeListener<LocalDate> fromDateChangeListener;
    private ChangeListener<LocalDate> toDateChangeListener;

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
        nameField.setText(entity.getName());
        nameField.setOnAction(event -> {
            entity.setName(nameField.getText());
        });
        descriptionField.setText(entity.getDescription());
        descriptionField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode()
                        .equals(KeyCode.ENTER))
                entity.setDescription(descriptionField.getText());
        });

        Optional<EntityImage> flagOptional = Optional.ofNullable(entity.getFlag());
        flagOptional.ifPresent(flag -> {
            flagView.imageProperty()
                    .setValue(flag
                            .getImage());
            Tooltip.install(flagView, new Tooltip(flag
                    .getDescription()));
        });
        if (!flagOptional.isPresent()) {
            flagView.setImage(null);
        }

        picturesView.setItems(entity.getImages());
        picturesView.setCellFactory(param -> new PictureListViewCell(
                image -> {
                    entity.getImages()
                          .remove(image);
                }, image -> {
            flagView.imageProperty()
                    .setValue(image.getImage());
            Tooltip.install(flagView, new Tooltip(image.getDescription()));
            entity.flagProperty()
                  .setValue(image);
        }));
        fromDateChangeListener = (observable, oldValue, newValue) -> {
            entity.setFrom(fromDate.getValue());
        };
        fromDate.valueProperty()
                .addListener(fromDateChangeListener);
        toDateChangeListener = (observable, oldValue, newValue) -> {
            entity.setTo(toDate.getValue());
        };
        toDate.valueProperty()
              .addListener(toDateChangeListener);

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
        Optional<EntityImage> entityImage = Optional.ofNullable(selectedEntity.flagProperty()
                                                                              .get());
        entityImage.ifPresent(image -> {
            flagView.imageProperty()
                    .unbindBidirectional(image
                            .imageProperty());
        });
        fromDate.valueProperty()
                .removeListener(fromDateChangeListener);
        toDate.valueProperty()
              .removeListener(toDateChangeListener);
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
        LoadPictureView loadPictureView = new LoadPictureView();
        Stage stage = new Stage();
        Scene scene = new Scene(loadPictureView.getView());
        stage.setTitle("Load new picture");
        stage.setScene(scene);

        LoadPicturePresenter presenter = (LoadPicturePresenter) loadPictureView.getPresenter();
        presenter.setStage(stage);

        // make the dialog modal
        stage.initOwner(mainStage.getOwner());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        presenter.getResult()
                 .ifPresent(result -> {
                     picturesView.getItems()
                                 .add(result);
                 });

    }
}

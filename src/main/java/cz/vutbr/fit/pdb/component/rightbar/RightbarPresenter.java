package cz.vutbr.fit.pdb.component.rightbar;

import cz.vutbr.fit.pdb.component.loadpicture.LoadPicturePresenter;
import cz.vutbr.fit.pdb.component.loadpicture.LoadPictureView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.circleinfo.CircleInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo.LineInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo.PointInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo.PolygonInfoView;
import cz.vutbr.fit.pdb.component.rightbar.geometry.rectangleinfo.RectangleInfoView;
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

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

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
        flagView.setOnContextMenuRequested(null);
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
            Entity copy = entity.copyOf();
            copy.setName(nameField.getText());
            entityService.updateEntity(copy, "name",
                    (newEntity) -> {
                        showInfo("Entity updated", "Entity updated successfully");
                        entity.setName(nameField.getText());
                    },
                    () -> {
                        showError("Database error", "Could not update entity");
                        nameField.setText(entity.getName());
                    });

        });
        descriptionField.setText(entity.getDescription());
        descriptionField.setOnKeyReleased(keyEvent -> {
            Entity copy = entity.copyOf();
            copy.setDescription(descriptionField.getText());
            if (keyEvent.getCode()
                        .equals(KeyCode.ENTER)) {
                entityService.updateEntity(copy, "description",
                        (newEntity) -> {
                            showInfo("Entity updated", "Entity updated successfully");
                            entity.setDescription(descriptionField.getText());
                        },
                        () -> {
                            showError("Database error", "Could not update entity");
                            descriptionField.setText(entity.getDescription());
                        });
            }
        });

        Optional<EntityImage> flagOptional = Optional.ofNullable(entity.getFlag());
        flagOptional.ifPresent(flag -> {
            flagView.imageProperty()
                    .setValue(flag
                            .getImage());
            Tooltip.install(flagView, new Tooltip(flag
                    .getDescription()));
            initFlagViewContextMenu(flag);

        });
        if (!flagOptional.isPresent()) {
            flagView.setImage(null);
        }

        picturesView.setItems(entity.getImages());
        picturesView.setCellFactory(param -> new PictureListViewCell(
                image -> {
                    entityService.removeImage(image,
                            () -> {
                                showInfo("Entity updated", "Entity updated successfully");
                                entity.getImages()
                                      .remove(image);
                            },
                            () -> {
                                showError("Database error", "Could not update entity");
                            });
                }, image -> {
            entityService.setAsFlag(selectedEntity.getId(), image, () -> {
                        showInfo("Entity updated", "Flag set successfully");
                        flagView.setImage(image.getImage());
                        Tooltip.install(flagView, new Tooltip(image.getDescription()));
                        entity.setFlag(image);
                        entity.getImages()
                              .remove(image);
                        initFlagViewContextMenu(image);
                    },
                    () -> {
                        showError("Database error", "Could not update entity");
                    });
        }, ((entityImage, imageOperation) -> {
            entityService.editImage(entityImage, imageOperation,
                    (newImage) -> {
                        entity.getImages()
                              .remove(entityImage);
                        entity.getImages()
                              .add(newImage);
                        showInfo("Picture updated", "Picture updated successfully");
                    }, () -> {
                        showError("Database error", "Could not edit picture");
                    });
        })));
        fromDateChangeListener = (observable, oldValue, newValue) -> {
            Entity copy = entity.copyOf();
            copy.setFrom(newValue);
            entityService.updateEntity(copy, "from",
                    (newEntity) -> {
                        showInfo("Entity updated", "Entity updated successfully");
                        entity.setFrom(fromDate.getValue());
                    },
                    () -> {
                        showError("Database error", "Could not update entity");
                        fromDate.setValue(entity.getFrom());
                    });

        };
        fromDate.setValue(entity.getFrom());
        fromDate.valueProperty()
                .addListener(fromDateChangeListener);
        toDateChangeListener = (observable, oldValue, newValue) -> {
            Entity copy = entity.copyOf();
            copy.setTo(newValue);
            entityService.updateEntity(copy, "to",
                    (newEntity) -> {
                        showInfo("Entity updated", "Entity updated successfully");
                        entity.setTo(toDate.getValue());
                    },
                    () -> {
                        showError("Database error", "Could not update entity");
                        toDate.setValue(entity.getTo());
                    });
        };
        toDate.setValue(entity.getTo());
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

            case RECTANGLE:
                initForRectangle();
                break;

            default:
                throw new RuntimeException();
        }
    }

    private void initFlagViewContextMenu(EntityImage flag) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteFlagMenuItem = new MenuItem("Delete flag");
        deleteFlagMenuItem.setOnAction(event -> {
            entityService.deleteFlag(flag,
                    () -> {
                        flagView.setImage(null);
                        flagView.setOnContextMenuRequested(null);
                        showInfo("Entity updated", "Entity updated successfully");
                    },
                    () -> {
                        showError("Database error", "Could not update entity");
                    });
        });
        contextMenu.getItems()
                   .add(deleteFlagMenuItem);

        flagView.setOnContextMenuRequested(contextMenuEvent ->
                contextMenu.show(flagView, contextMenuEvent.getSceneX(), contextMenuEvent.getSceneY()));
    }

    private void unbindAll() {
        if (selectedEntity == null) {
            log.severe("Selected entity is null, cannot unbind");
            return;
        }
        nameField.setOnAction(null);
        descriptionField.setOnKeyReleased(null);

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

    private void initForRectangle() {
        geometryTitledPane.setContent(new RectangleInfoView().getView());
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
                     log.info("Saving new image");
                     entityService.addImage(selectedEntity.getId(), result,
                             () -> {
                                 showInfo("Entity updated", "Entity updated successfully");
                                 picturesView.getItems()
                                             .add(result);
                             },
                             () -> {
                                 showError("Database error", "Could not update entity");
                             });


                 });

    }
}

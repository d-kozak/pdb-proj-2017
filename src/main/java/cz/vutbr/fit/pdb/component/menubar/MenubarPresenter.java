package cz.vutbr.fit.pdb.component.menubar;

import cz.vutbr.fit.pdb.component.settings.SettingsView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.concurent.InitDatabaseTask;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

@Log
public class MenubarPresenter {

    @Inject
    private EntityService entityService;

    @Inject
    private Stage primaryStage;

    @Inject
    private Configuration configuration;

    @FXML
    private void onInitDB(ActionEvent event) {
        log.info("Initializing the database");
        InitDatabaseTask initDatabaseTask = new InitDatabaseTask();
        initDatabaseTask.setOnSucceeded((stateEvent) -> {
            entityService.init();
            entityService.initDataLoadedProperty()
                         .addListener((observable, oldValue, isDataLoaded) -> {
                             if (isDataLoaded) {
                                 configuration.getMapRenderer()
                                              .redraw();
                                 showInfo("Success", "Database initialized successfully.");
                             }
                         });
        });
        initDatabaseTask.setOnFailed((stateEvent) -> {
            showError("Failure", "Database initialization FAILED!");
        });
        showInfo("Initialization started", "Please wait a little");
        Configuration.THREAD_POOL.submit(initDatabaseTask);
    }

    @FXML
    private void onSettings(ActionEvent event) {
        JavaFXUtils.openModalDialog(primaryStage, "Settings", new SettingsView());
    }

    @FXML
    private void onClose(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onClearAll(ActionEvent event) {
        log.info("Deleting all entities");
        entityService.getEntities()
                     .clear();
        configuration.getMapRenderer()
                     .redraw();
    }


    @FXML
    private void onExportPng(ActionEvent event) {
        val fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                   .add(
                           new FileChooser.ExtensionFilter("png files (*.png)", "*.png")
                   );
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage(configuration.getCanvasWidth(), configuration.getCanvasHeight());
                configuration.getMap()
                             .snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
                showInfo("Success", "Image exported successfully");
            } catch (IOException ex) {
                showError("Export error", "Could not export image");
                log.severe("Could not save image");
            }
        }
    }


    @FXML
    private void onShowTutorial(ActionEvent event) {
        notImpl();
    }


    @FXML
    private void onShowAbout(ActionEvent event) {
        notImpl();
    }

    private void notImpl() {
        showError("Error", "Not impl yet!");
        log.severe("Not impl yet!");
    }
}

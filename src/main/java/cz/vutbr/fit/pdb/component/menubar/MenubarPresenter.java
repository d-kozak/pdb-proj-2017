package cz.vutbr.fit.pdb.component.menubar;

import cz.vutbr.fit.pdb.component.settings.SettingsView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.db.DBConnection;
import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.EntityService;
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
        MapMakerDB db = MapMakerDB.create();
        db.initDB("init_db.sql");
    }

    @FXML
    private void onSettings(ActionEvent event) {
        JavaFXUtils.openModalDialog(primaryStage, "Settings", new SettingsView());
    }

    @FXML
    private void onClose(ActionEvent event) {
        DBConnection dbConnection = DBConnection.create();
        dbConnection.disconnect();
        Platform.exit();
    }


    @FXML
    private void onShowTerminal(ActionEvent event) {

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
                configuration.getCanvas()
                             .snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                log.severe("Could not save image");
            }
        }
    }


    @FXML
    private void onShowTutorial(ActionEvent event) {

    }


    @FXML
    private void onShowAbout(ActionEvent event) {

    }

    @FXML
    private void onLoadEntities(ActionEvent event) {
        entityService.init();
    }
}

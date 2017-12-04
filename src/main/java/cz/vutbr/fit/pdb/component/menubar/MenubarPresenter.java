package cz.vutbr.fit.pdb.component.menubar;

import cz.vutbr.fit.pdb.db.DBConnection;
import cz.vutbr.fit.pdb.db.MapMakerDB;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class MenubarPresenter {

    @Inject
    private EntityService entityService;

    @FXML
    private void onInitDB(ActionEvent event) {
        MapMakerDB db = MapMakerDB.create();
        db.initDB("init_db.sql");
    }

    @FXML
    private void onSettings(ActionEvent event) {

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

    }


    @FXML
    private void onExportPng(ActionEvent event) {

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

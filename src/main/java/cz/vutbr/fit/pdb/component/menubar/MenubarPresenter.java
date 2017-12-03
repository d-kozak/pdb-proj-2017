package cz.vutbr.fit.pdb.component.menubar;

import cz.vutbr.fit.pdb.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenubarPresenter {
    @FXML
    private void onInitDB(ActionEvent event){
        DBConnection dbConn = DBConnection.create();
        dbConn.initDB("init_db.sql");
    }

    @FXML
    private void onSettings(ActionEvent event) {

    }

    @FXML
    private void onClose(ActionEvent event) {

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
}

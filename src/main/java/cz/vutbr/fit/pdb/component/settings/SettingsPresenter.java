package cz.vutbr.fit.pdb.component.settings;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DBConfiguration;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class SettingsPresenter implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField databaseURIField;

    @Inject
    private Configuration configuration;

    private DBConfiguration dbConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbConfiguration = configuration.getDbConfiguration();
        log.info("Loaded db configuration: " + dbConfiguration);
        usernameField.setText(dbConfiguration.getUsername());
        passwordField.setText(dbConfiguration.getPassword());
        databaseURIField.setText(dbConfiguration.getDatabaseURI());
    }

    public void onConfirm(ActionEvent event) {
        dbConfiguration.setUsername(usernameField.getText());
        dbConfiguration.setPassword(passwordField.getText());
        dbConfiguration.setDatabaseURI(databaseURIField.getText());

        log.info("New db configuration : " + dbConfiguration);
        JavaFXUtils.closeWindow(event);
    }

    public void onCancel(ActionEvent event) {
        JavaFXUtils.closeWindow(event);
    }
}

package cz.vutbr.fit.pdb.component.settings;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DBConfiguration;
import cz.vutbr.fit.pdb.db.DBConnection;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;

@Log
public class SettingsPresenter implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField serviceNameField;

    private IntegerProperty portNum = new SimpleIntegerProperty();

    @FXML
    private Text fullURIText;

    @Inject
    private Configuration configuration;

    private DBConfiguration dbConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbConfiguration = configuration.getDbConfiguration();
        log.info("Loaded db configuration: " + dbConfiguration);

        ChangeListener<String> createDnURIListener = (observable, oldValue, newValue) -> {
            fullURIText.setText(createDatabaseURI());
        };
        hostField.textProperty()
                 .addListener(createDnURIListener);
        portField.textProperty()
                 .addListener(createDnURIListener);
        serviceNameField.textProperty()
                        .addListener(createDnURIListener);

        this.portNum = new SimpleIntegerProperty(dbConfiguration.getPort());
        usernameField.setText(dbConfiguration.getUsername());
        passwordField.setText(dbConfiguration.getPassword());
        hostField.setText(dbConfiguration.getHost());
        portField.textProperty()
                 .bindBidirectional(portNum, new StringNumConverter());
        serviceNameField.setText(dbConfiguration.getServiceName());

    }

    private String createDatabaseURI() {
        return "jdbc:oracle:thin:@//" + hostField.getText() + ":" + portNum.get() + "/" + serviceNameField.getText();
    }

    public void onConfirm(ActionEvent event) {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            if (!dbConnection.disconnect()) {
                throw new RuntimeException("Could not disconnect");
            }
            if (!dbConnection.connect(hostField.getText(),
                    portNum.get() + "",
                    serviceNameField.getText(),
                    usernameField.getText(),
                    passwordField.getText())
                    ) {
                throw new RuntimeException("Could not connect");
            }

            dbConfiguration.setUsername(usernameField.getText());
            dbConfiguration.setPassword(passwordField.getText());
            dbConfiguration.setHost(hostField.getText());
            dbConfiguration.setPort(portNum.get());
            dbConfiguration.setServiceName(serviceNameField.getText());
            log.info("New db configuration : " + dbConfiguration);
            JavaFXUtils.closeWindow(event);
        } catch (RuntimeException ex) {
            log.severe("Error: " + ex.getMessage());
            showError("Database error", "Please check that all the fields are correct");

        }
    }

    public void onCancel(ActionEvent event) {
        JavaFXUtils.closeWindow(event);
    }
}

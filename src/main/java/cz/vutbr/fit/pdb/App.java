package cz.vutbr.fit.pdb;

import com.airhacks.afterburner.injection.Injector;
import cz.vutbr.fit.pdb.component.main.MainPresenter;
import cz.vutbr.fit.pdb.component.main.MainView;
import cz.vutbr.fit.pdb.component.settings.SettingsView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DBConfiguration;
import cz.vutbr.fit.pdb.db.DBConnection;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;

/**
 * Hello darkness, my old friend, I've come to talk with you again...
 */
@Log
public class App extends Application {

    public static void main(String[] args) throws SQLException {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            boolean succeeded = dbConnection.connect(
                    DBConfiguration.HOST_DEFAULT,
                    DBConfiguration.PORT_DEFAULT + "",
                    DBConfiguration.SERVICE_NAME_DEFAULT,
                    DBConfiguration.USERNAME_DEFAULT,
                    DBConfiguration.PASSWORD_DEFAULT
            );
            if (!succeeded) {
                throw new RuntimeException();

            }
        } catch (RuntimeException ex) {
            log.severe("Connection failed!");
            log.severe("Exception " + ex.getMessage());
        }

        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        val view = new MainView();
        val scene = new Scene(view.getView());
        initAfterBurner(mainStage);
        //AquaFx.style();
        mainStage.setTitle("MapMaker");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
        checkAndFixDbConnection(mainStage, ((MainPresenter) view.getPresenter()));
    }

    private void checkAndFixDbConnection(Stage mainStage, MainPresenter mainPresenter) {
        log.info("Verifying db connection");
        DBConnection dbConnection = DBConnection.getInstance();
        if (!dbConnection.isConnected()) {
            while (!dbConnection.isConnected()) {
                showError("Wrong credentials", "Please correct your database credentials and check the internet connection");
                JavaFXUtils.openModalDialog(mainStage, "Settings", new SettingsView());
            }
            log.info("DB connection was fixed successfully");
            mainPresenter.reload();
        } else
            log.info("DB connection is fine");

    }

    private void initAfterBurner(Stage mainStage) {
        Logger logger = Logger.getLogger(Injector.class.getName());
        //Injector.setLogger(logger::info);
        Map<Object, Object> toInject = new HashMap<>();

        toInject.put("mainStage", mainStage);
        // add objects for DI if needed

        Injector.setConfigurationSource(toInject::get);
    }

    @Override
    public void stop() throws Exception {
        log.info("Preparing to close the application...");
        log.info("Closing the database connection");
        if (!DBConnection.getInstance()
                         .disconnect()) {
            log.severe("Could not disconnect from the database");
        } else
            log.info("Success...");
        log.info("Shutting down the thread pool...");
        Configuration.THREAD_POOL.shutdown();
        try {
            Configuration.THREAD_POOL.awaitTermination(3, TimeUnit.SECONDS);
            log.info("Success...");
        } catch (InterruptedException ex) {
            log.severe("Thread pool did not shutdown in down, using force shutdown");
            Configuration.THREAD_POOL.shutdownNow();
        }
        log.info("Cleanup finished, closing the application...");
        System.exit(0);
    }
}

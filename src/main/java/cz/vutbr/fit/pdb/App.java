package cz.vutbr.fit.pdb;

import com.airhacks.afterburner.injection.Injector;
import cz.vutbr.fit.pdb.component.main.MainView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.db.DBConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Hello darkness, my old friend, I've come to talk with you again...
 */
@Log
public class App extends Application {

    public static void main(String[] args) throws SQLException {
        DBConnection dbConnection = DBConnection.getInstance();
        boolean succeeded = dbConnection.connect(
                "gort.fit.vutbr.cz",
                "1521",
                "gort.fit.vutbr.cz",
                System.getProperty("username"),
                System.getProperty("password")
        );
        if (!succeeded) {
            log.severe("Connection failed!");
            return;
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
        DBConnection.getInstance()
                    .disconnect();
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
    }
}

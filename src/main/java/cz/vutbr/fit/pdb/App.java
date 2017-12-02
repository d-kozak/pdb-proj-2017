package cz.vutbr.fit.pdb;

import com.airhacks.afterburner.injection.Injector;
import cz.vutbr.fit.pdb.component.main.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hello darkness, my old friend, I've come to talk with you again...
 */
@Log
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        val view = new MainView();
        val scene = new Scene(view.getView());
        initAfterBurner(mainStage);
        mainStage.setTitle("MapMaker");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
    }

    private void initAfterBurner(Stage mainStage) {
        Logger logger = Logger.getLogger(Injector.class.getName());
        Injector.setLogger(logger::info);
        Map<Object, Object> toInject = new HashMap<>();

        toInject.put("mainStage", mainStage);
        // add objects for DI if needed

        Injector.setConfigurationSource(toInject::get);
    }
}

package cz.vutbr.fit.pdb.utils;

import com.airhacks.afterburner.views.FXMLView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import org.controlsfx.control.Notifications;

import java.util.concurrent.CompletableFuture;

@Log
public class JavaFXUtils {
    public static void openModalDialog(Stage primaryStage, String title, FXMLView fxmlView) {
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlView.getView());
        stage.setTitle(title);
        stage.setScene(scene);

        // make the dialog modal
        stage.initOwner(primaryStage.getOwner());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public static void runLaterOnUiThread(long milis, Runnable runnable) {
        Configuration.THREAD_POOL.submit(() -> {
            try {
                Thread.sleep(milis);
                Platform.runLater(runnable);
            } catch (InterruptedException e) {
                log.severe("sleep interrupted " + e);
            }
        });
    }

    public static void closeWindow(ActionEvent event) {
        ((Node) event.getSource()).getScene()
                                  .getWindow()
                                  .hide();
    }

    public static void showError(String title, String text) {
        Notifications.create()
                     .title(title)
                     .text(text)
                     .darkStyle()
                     .showError();
    }

    public static void showInfo(String title, String text) {
        Notifications.create()
                     .title(title)
                     .text(text)
                     .darkStyle()
                     .showInformation();
    }

    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void startWithTimeout(final long millis, Task task) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(millis);
                if (!task.isDone()) {
                    log.severe("Cancelling task that did not finish until " + millis + " millis");
                    task.cancel(true);
                } else {
                    log.info("Task exited in time");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}

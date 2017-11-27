package cz.vutbr.fit.pdb.component.main;

import cz.vutbr.fit.pdb.component.menubar.MenubarView;
import cz.vutbr.fit.pdb.component.toolbar.ToolbarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import lombok.val;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class MainPresenter implements Initializable {

    @Inject
    private Stage mainStage;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("mainStage=" + mainStage);
        val toolbarView = new ToolbarView();
        val menubarView = new MenubarView();
        VBox vBox = new VBox();
        vBox.getChildren()
            .addAll(menubarView.getView(), toolbarView.getView());
        borderPane.setTop(vBox);
    }

}

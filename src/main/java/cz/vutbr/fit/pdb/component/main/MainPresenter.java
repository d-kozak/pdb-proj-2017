package cz.vutbr.fit.pdb.component.main;

import cz.vutbr.fit.pdb.component.blindmapcontrol.BlindMapControlView;
import cz.vutbr.fit.pdb.component.bottombar.BottombarView;
import cz.vutbr.fit.pdb.component.map.MapView;
import cz.vutbr.fit.pdb.component.menubar.MenubarView;
import cz.vutbr.fit.pdb.component.rightbar.RightbarView;
import cz.vutbr.fit.pdb.component.toolbar.ToolbarView;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.extern.java.Log;
import lombok.val;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class MainPresenter implements Initializable {

    @Inject
    private Configuration configuration;

    @Inject
    private EntityService entityService;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        val mapView = new MapView();
        borderPane.setCenter(mapView.getView());

        val toolbarView = new ToolbarView();
        val menubarView = new MenubarView();
        HBox hBox = new HBox();
        hBox.getChildren()
            .addAll(menubarView.getView(), toolbarView.getView());
        borderPane.setTop(hBox);

        val bottombarView = new BottombarView();
        borderPane.setBottom(bottombarView.getView());

        val rightbarView = new RightbarView();
        borderPane.setRight(rightbarView.getView());

        configuration.appModeProperty()
                     .addListener((observable, oldValue, newValue) -> {
                         switch (newValue) {
                             case BLIND_MAP:
                                 val blindmapControlView = new BlindMapControlView();
                                 borderPane.setRight(blindmapControlView.getView());
                                 break;
                             case VIEW:
                             case EDIT:
                                 val newRightbarView = new RightbarView();
                                 borderPane.setRight(newRightbarView.getView());
                                 break;
                             default:
                                 throw new RuntimeException("Default in switch");
                         }
                     });
    }

    public void reload() {
        entityService.init();
        entityService.initDataLoadedProperty()
                     .addListener((observable, oldValue, isDataLoaded) -> {
                         if (isDataLoaded) {
                             configuration.getMapRenderer()
                                          .redraw();
                         }
                     });
    }
}

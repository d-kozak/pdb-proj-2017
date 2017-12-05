package cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo;

import cz.vutbr.fit.pdb.component.rightbar.pointlistitem.PointListViewCell;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.Listeners;
import cz.vutbr.fit.pdb.utils.MathUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


@Log
public class LineInfoPresenter implements Initializable {
    @FXML
    private ListView<Point> lineListView;
    @FXML
    private TextField lineXField;
    @FXML
    private TextField lineYField;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private Configuration configuration;

    private ObservableList<Point> points;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        points = (ObservableList<Point>) selectedEntityService.getEntityProperty()
                                                              .getGeometry()
                                                              .getDescription();
        lineListView.setItems(points);
        lineListView.setCellFactory(item -> new PointListViewCell(configuration, points::remove));

        Listeners.addRedrawListener(configuration.getMapRenderer(), points);
    }

    public void addLinePoint(ActionEvent event) {
        Optional<Point> point = Point.of(lineXField.getText(), lineYField.getText());
        point.ifPresent(p -> MathUtils.addNewPointFromFields(p, lineXField, lineYField, points, configuration));
    }
}

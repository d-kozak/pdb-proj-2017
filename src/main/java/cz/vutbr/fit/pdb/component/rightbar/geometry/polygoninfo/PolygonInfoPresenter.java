package cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo;

import cz.vutbr.fit.pdb.component.rightbar.pointlistitem.PointListViewCell;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PolygonInfoPresenter implements Initializable {
    @FXML
    private ListView<Point> polygonListView;
    @FXML
    private TextField polygonXField;
    @FXML
    private TextField polygonYField;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private Configuration configuration;

    private ObservableList<Point> points;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        points = ((ObservableList<Point>) selectedEntityService.getEntityProperty()
                                                               .getGeometry()
                                                               .getDescription());
        polygonListView.setCellFactory(item -> new PointListViewCell(configuration, points::remove));
    }

    public void addPolygonPoint(ActionEvent event) {
        Optional<Point> point = Point.of(polygonXField.getText(), polygonYField.getText());
        point.ifPresent(points::add);
    }
}

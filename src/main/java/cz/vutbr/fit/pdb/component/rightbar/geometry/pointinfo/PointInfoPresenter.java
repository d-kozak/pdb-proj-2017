package cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo;

import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class PointInfoPresenter implements Initializable {

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Point point = (Point) selectedEntityService.getEntityProperty()
                                                   .getGeometry()
                                                   .getDescription();
        xField.textProperty()
              .bindBidirectional(point.xProperty(), new StringNumConverter());
        yField.textProperty()
              .bindBidirectional(point.yProperty(), new StringNumConverter());
    }
}

package cz.vutbr.fit.pdb.component.rightbar.geometry.circleinfo;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.Listeners;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class CircleInfoPresenter implements Initializable {
    @FXML
    private TextField centerXField;
    @FXML
    private TextField centerYField;
    @FXML
    private TextField radiusField;

    @Inject
    private Configuration configuration;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Object[] description = (Object[]) selectedEntityService.getEntityProperty()
                                                               .getGeometry()
                                                               .getDescription();
        Point center = ((Point) description[0]);
        DoubleProperty radius = (DoubleProperty) description[1];
        centerXField.textProperty()
                    .bindBidirectional(center.xProperty(), new StringNumConverter());
        centerYField.textProperty()
                    .bindBidirectional(center.yProperty(), new StringNumConverter());
        radiusField.textProperty()
                   .bindBidirectional(radius, new StringNumConverter());

        Listeners.addRedrawListener(configuration.getMapRenderer(), centerXField.textProperty(), centerYField.textProperty(), radiusField.textProperty());
    }
}

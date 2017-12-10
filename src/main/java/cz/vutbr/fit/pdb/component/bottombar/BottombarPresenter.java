package cz.vutbr.fit.pdb.component.bottombar;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class BottombarPresenter implements Initializable {

    @FXML
    private Slider yearSlider;

    @FXML
    private TextField yearTextField;

    @Inject
    private Configuration configuration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        yearSlider.minProperty()
                  .bindBidirectional(configuration.minimumYearProperty());
        yearSlider.maxProperty()
                  .bindBidirectional(configuration.maximumYearProperty());

        yearSlider.valueProperty()
                  .addListener((obs, oldval, newVal) ->
                          yearSlider.setValue(Math.round(newVal.doubleValue())));

        yearSlider.valueProperty()
                  .bindBidirectional(configuration.yearProperty());

        yearTextField.textProperty()
                     .bindBidirectional(yearSlider.valueProperty(), new StringNumConverter());


    }
}

package cz.vutbr.fit.pdb.component.main;

import javafx.stage.Stage;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Log
public class MainPresenter {

    @Inject
    private Stage mainStage;

    @PostConstruct
    public void init() {
        log.info("mainStage=" + mainStage);
    }
}

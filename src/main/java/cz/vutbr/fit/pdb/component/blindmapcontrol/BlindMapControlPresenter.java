package cz.vutbr.fit.pdb.component.blindmapcontrol;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.EntityService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;
import static cz.vutbr.fit.pdb.utils.ListUtils.randomElementFromList;
import static java.util.stream.Collectors.toList;

@Log
public class BlindMapControlPresenter implements Initializable {
    @FXML
    private Text correctAnswers;
    @FXML
    private Text titleText;
    @FXML
    private ImageView imageView1;
    @FXML
    private ImageView imageView2;
    @FXML
    private ImageView imageView3;
    @Inject
    private EntityService entityService;

    private IntegerProperty correctAnswersCount = new SimpleIntegerProperty(0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chooseCorrectImageExercise();
        correctAnswers.setText("Number of correct answers: 0");
        correctAnswersCount.addListener((observable, oldValue, newValue) -> {
            correctAnswers.setText("Number of correct answers: " + newValue.intValue());
        });
    }

    private void chooseCorrectImageExercise() {
        titleText.setText("Loading...");
        List<Entity> entitiesWithImages = entityService.getEntities()
                                                       .stream()
                                                       .filter(entity -> entity.getImages()
                                                                               .size() > 0)
                                                       .collect(toList());
        Entity randomEntity = randomElementFromList(entitiesWithImages);
        EntityImage randomImage = randomElementFromList(randomEntity.getImages());

        entityService.getTwoSimilarImagesFor(randomImage,
                (similarImages) -> {
                    initImageViews(randomEntity.getName(), randomImage, similarImages);
                }, () -> {
                    showError("Database error", "Sorry, connection failed");
                });
    }

    private void initImageViews(String name, EntityImage correctImage, ObservableList<EntityImage> similarImages) {
        if (similarImages.size() < 2)
            throw new RuntimeException("Two similar images needed");
        titleText.setText("Select which picture is from " + name);
        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);
        imageViews.forEach(imageView -> imageView.setOnMouseClicked(null));

        ImageView imageView = randomElementFromList(imageViews);
        imageViews.remove(imageView);
        imageView.setImage(correctImage.getImage());
        imageView.setOnMouseClicked(event -> {
            chooseCorrectImageExercise();
            correctAnswersCount.setValue(correctAnswersCount.get() + 1);
            imageView1.setImage(null);
            imageView2.setImage(null);
            imageView3.setImage(null);
            showInfo("Correct", "Try another one");
        });

        imageView = randomElementFromList(imageViews);
        imageViews.remove(imageView);
        imageView.setImage(similarImages.get(0)
                                        .getImage());

        imageView.setOnMouseClicked(event -> {
            showError("Incorrect", "Try it again :)");
        });

        imageView = imageViews.get(0);
        imageViews.remove(imageView);
        imageView.setImage(similarImages.get(1)
                                        .getImage());
        imageView.setOnMouseClicked(event -> {
            showError("Incorrect", "Try it again :)");
        });
    }


}

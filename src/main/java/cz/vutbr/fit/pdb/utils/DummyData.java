package cz.vutbr.fit.pdb.utils;

import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityImage;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.val;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;

public class DummyData {

    private static ObservableList<Entity> entities = FXCollections.observableArrayList();

    static {
        entities.add(loadDummyEntity());
    }

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    public static ObservableList<Entity> getEntities() {
        return entities;
    }

    private static Entity loadDummyEntity() {
        Entity entity = new Entity();
        entity.setName("Brno");
        entity.setDescription("The best town in the Czech Republic");
        entity.setGeometry(new PointGeometry(new Point(42, 42)));
        try {
            val image = new Image(new FileInputStream("brno-flag.jpg"));
            val entityImage = new EntityImage();
            entityImage.setImage(image);
            entityImage.setDescription("dummy description");
            entity.setFlag(entityImage);

            ObservableList<EntityImage> images = FXCollections.observableArrayList();
            for (int i = 0; i < 3; i++) {
                EntityImage entityImage1 = new EntityImage();
                entityImage1.setImage(image);
                entityImage1.setDescription("dummy description" + i);
                images.add(entityImage1);
            }
            entity.setImages(images);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        entity.setFrom(LocalDate.of(42, 11, 1));
        entity.setTo(LocalDate.of(2000, 1, 1));

        return entity;
    }
}

package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.Configuration;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;

public class SelectedEntityService {
    private ObjectProperty<Entity> entityProperty = new SimpleObjectProperty<>();

    @Inject
    private Configuration configuration;

    @PostConstruct
    public void init() {
        configuration.yearProperty()
                     .addListener((observable, oldValue, newValue) -> {
                         Entity entity = entityProperty.get();
                         if (entity != null) {
                             LocalDate from = entity.getFrom();
                             LocalDate to = entity.getTo();
                             if (newValue.intValue() < from.getYear() || newValue.intValue() > to.getYear()) {
                                 entityProperty.set(null);
                             }
                         }
                     });
    }

    public Entity getEntityProperty() {
        return entityProperty.get();
    }

    public void setEntityProperty(Entity entityProperty) {
        this.entityProperty.set(entityProperty);
    }

    public ObjectProperty<Entity> entityPropertyProperty() {
        return entityProperty;
    }
}

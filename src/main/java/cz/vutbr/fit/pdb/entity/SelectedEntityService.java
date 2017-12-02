package cz.vutbr.fit.pdb.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class SelectedEntityService {
    private ObjectProperty<Entity> objectProperty = new SimpleObjectProperty<>();

    @Inject
    private EntityService entityService;

    @PostConstruct
    public void init() {
        objectProperty.setValue(entityService.getEntities()
                                             .get(0));
    }

    public Entity getObjectProperty() {
        return objectProperty.get();
    }

    public void setObjectProperty(Entity objectProperty) {
        this.objectProperty.set(objectProperty);
    }

    public ObjectProperty<Entity> objectPropertyProperty() {
        return objectProperty;
    }
}

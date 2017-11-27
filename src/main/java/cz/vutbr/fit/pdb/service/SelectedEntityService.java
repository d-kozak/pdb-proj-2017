package cz.vutbr.fit.pdb.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectedEntityService {
    private ObjectProperty<Object> objectProperty = new SimpleObjectProperty<>();

    public Object getObjectProperty() {
        return objectProperty.get();
    }

    public void setObjectProperty(Object objectProperty) {
        this.objectProperty.set(objectProperty);
    }

    public ObjectProperty<Object> objectPropertyProperty() {
        return objectProperty;
    }
}

package cz.vutbr.fit.pdb.utils;

import cz.vutbr.fit.pdb.entity.Entity;
import javafx.util.StringConverter;

public class StringEntityConverter extends StringConverter<Entity> {
    public static final StringEntityConverter INSTANCE = new StringEntityConverter();

    @Override
    public String toString(Entity entity) {
        return entity.getName();
    }

    @Override
    public Entity fromString(String string) {
        throw new RuntimeException("This converter is Entity => String only");
    }
}

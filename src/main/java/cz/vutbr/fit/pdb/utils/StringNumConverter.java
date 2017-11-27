package cz.vutbr.fit.pdb.utils;

import javafx.util.StringConverter;

public class StringNumConverter extends StringConverter<Number> {
    @Override
    public String toString(Number object) {
        return object.intValue() + "";
    }

    @Override
    public Number fromString(String string) {
        try {
            return new Integer(string);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}

package cz.vutbr.fit.pdb.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReflectionUtilsTest {

    private Person person;

    @Before
    public void init() {
        person = new Person("Pepa");
    }

    @Test
    public void correctBehavior() {

        assertEquals("Should be Pepa", "Pepa", ReflectionUtils.getValue(person, "name"));
        ReflectionUtils.setValue(person, "name", "Ondra");
        assertEquals("Should be Ondra", "Ondra", ReflectionUtils.getValue(person, "name"));
    }


    @Test(expected = RuntimeException.class)
    public void exceptionSetName() {
        ReflectionUtils.setValue(person, "unreal", "lalala");
    }

    @Test(expected = RuntimeException.class)
    public void exceptionGetName() {
        ReflectionUtils.getValue(person, "unreal");
    }
}


@Data
@AllArgsConstructor
class Person {
    private String name;
}

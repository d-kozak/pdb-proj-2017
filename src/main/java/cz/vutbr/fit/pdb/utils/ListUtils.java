package cz.vutbr.fit.pdb.utils;

import java.util.List;
import java.util.Random;

public class ListUtils {
    private static final Random random = new Random();

    public static <T> T randomElementFromList(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}

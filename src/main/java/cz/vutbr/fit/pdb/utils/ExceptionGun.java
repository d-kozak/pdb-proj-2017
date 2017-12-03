package cz.vutbr.fit.pdb.utils;

import lombok.extern.java.Log;

import java.util.Random;

@Log
public class ExceptionGun {

    private static final Random random = new Random();

    public static void throwMeMaybe() {
        if (random.nextDouble() < 0.5) {
            log.severe("Muheheheh...");
            throw new RuntimeException("Hey, I just met you and this is crazy...");
        }
    }
}

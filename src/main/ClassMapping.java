package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClassMapping {
    static final String path = ".\\classes.txt";
    static final int numberOfClasses = 26;
    static Map<Integer, Integer> realToModel = new HashMap<>();
    static Map<Integer, Integer> modelToReal = new HashMap<>();

    static {
        try {
            Scanner scanner = new Scanner(new File(path));
            for (int i = 0; i < numberOfClasses; ++i) {
                Integer real = scanner.nextInt();
                realToModel.put(real, i);
                modelToReal.put(i, real);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        realToModel = Collections.unmodifiableMap(realToModel);
        modelToReal = Collections.unmodifiableMap(modelToReal);
    }

    public static int toModel(int real) {
        return realToModel.get(real);
    }

    public static int toReal(int model) {
        return modelToReal.get(model);
    }
}

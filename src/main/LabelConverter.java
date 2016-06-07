package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LabelConverter {
    public static List<Double> toList(int label, int classCount) {
        List<Double> res = Arrays.asList(new Double[classCount]);
        Collections.fill(res, 0.0);
        res.set(label, 1.0);
        return res;
    }

    public static int toLabel(List<Double> list) {
        return list.indexOf(Collections.max(list));
    }
}

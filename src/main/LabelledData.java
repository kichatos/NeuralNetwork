package main;

import java.util.ArrayList;
import java.util.List;

public class LabelledData {
    List<Double> data;
    List<Double> label;

    public LabelledData(List<Double> data, List<Double> label) {
        this.data = data;
        this.label = label;
    }

    public List<Double> getData() {
        return data;
    }

    public List<Double> getLabel() {
        return label;
    }
}

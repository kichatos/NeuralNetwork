package main;

import neuralnetwork.DBN;
import neuralnetwork.NeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Artem on 12.05.2016.
 */
public class Main {
    private static List<List<Double>> inputs;
    private static List<List<Double>> ans;

    public static void main(String[] args) {
        UnlabelledReader reader = new UnlabelledReader();
        reader.next();
        List<Integer> neurons = Arrays.asList(9, 81, 53, 26);
        DBN dbn = new DBN(neurons);
        while (reader.hasNext()) {
            dbn.preTrainNetwork(reader.next());
            dbn.save("" + (reader.getPosition() - 1) + "-" + reader.getFilePosition() + ".txt");
        }

//        DBN dbn = DBN.load("dbn.txt");

//        readData(".\\Data\\Iris.txt");
//        scale(inputs);

//        dbn.trainNetwork(inputs, ans);
//        dbn.testClassifier(inputs, ans);
//        dbn.save("dbn.txt");

    }

    private static void scale(List<List<Double>> data) {
        List<Double> min = new ArrayList<>();
        List<Double> max = new ArrayList<>();

        for (int j = 0; j < data.get(0).size(); ++j) {
            min.add(data.get(0).get(j));
            max.add(data.get(0).get(j));
            for (int i = 1; i < data.size(); ++i) {
                if (data.get(i).get(j) < min.get(j)) {
                    min.set(j, data.get(i).get(j));
                }

                if (data.get(i).get(j) > max.get(j)) {
                    max.set(j, data.get(i).get(j));
                }
            }
        }

        List<Double> width = new ArrayList<>();
        for (int j = 0; j < max.size(); ++j) {
            width.add(max.get(j) - min.get(j));
        }

        for (int i = 0; i < data.size(); ++i) {
            for (int j = 0; j < data.get(0).size(); ++j) {
                data.get(i).set(j, (data.get(i).get(j) - min.get(j)) / width.get(j));
            }
        }
    }

    private static void readData(String fileAddress) {
        inputs = new ArrayList<>();
        ans = new ArrayList<>();


        try {
            File file = new File(fileAddress);
            InputStream inp = new FileInputStream(file);
            Scanner scan = new Scanner(inp);

            while (scan.hasNextLine()) {
                String[] vals = scan.nextLine().split(" ");

                List<Double> input = new ArrayList<>();
                List<Double> answer = new ArrayList<>();
                answer.add(0.0);
                answer.add(0.0);
                answer.add(0.0);

                for (int i = 0; i < 4; ++i) {
                    input.add(Double.parseDouble(vals[i].trim()));
                }

                answer.set(Integer.parseInt(vals[4].trim()), 1.0);

                inputs.add(input);
                ans.add(answer);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

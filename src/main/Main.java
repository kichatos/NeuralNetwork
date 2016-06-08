package main;

import neuralnetwork.DBN;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<List<Double>> inputs;
    private static List<List<Double>> ans;

    public static void main(String[] args) {
        int fileNumber = 1;
        int lineNumber = 0;
//        UnlabelledReader reader = new UnlabelledReader(fileNumber, lineNumber);


        List<Integer> neurons = Arrays.asList(9, 81, 53, 26);
        DBN dbn = new DBN(neurons);
        List<LabelledData> labelledDataList = new LabelledReader().next();
        List<List<Double>> inputs = new ArrayList<>();
        List<List<Double>> answers = new ArrayList<>();
        for (LabelledData labelledData : labelledDataList) {
            inputs.add(labelledData.getData());
            answers.add(labelledData.getLabel());
        }

        dbn.trainNetwork(inputs, answers);
        dbn.testClassifier(inputs, answers);
        dbn.save("clean.txt");
//        DBN dbn = DBN.load("\\1-780000.txt");

//        String lastPath = null;
//        while (reader.hasNext()) {
//            int currentFile = reader.getFileNumber();
//            int currentLine = reader.getLineNumber();
//
//            dbn.preTrainNetwork(reader.next());
//            String path = "" + currentFile + "-" + currentLine + ".txt";
//            dbn.save(path);
//            if (lastPath != null) {
//                boolean deleted = new File(lastPath).delete() && new File(lastPath + "net.txt").delete();
//                if (!deleted) {
//                    System.out.println("Failed to remove previous network.");
//                }
//            }
//
//            lastPath = path;
//        }
    }
}

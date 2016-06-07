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

    /**
     * @param args args[0] - path to Unlablelled folder
     *             args[1] - "continue" or "new" //optional
     *             args[2] - path to previous network file //if only args[1] == "continue"
     *             args[3] - file number //if only args[1] == "continue"
     *             args[4] - line number //if only args[1] == "continue"
     */
    public static void main(String[] args) {
        int fileNumber = 1;
        int lineNumber = 0;
        String unlabelledPath;
        if (args.length > 0) {
            unlabelledPath = args[0];
            if (args.length == 5 && args[1].equals("continue")) {
                fileNumber = Integer.valueOf(args[3]);
                lineNumber = Integer.valueOf(args[4]);
            }
        } else {
            unlabelledPath = "E:\\Wsl_F\\new_unlabelled";
        }

        UnlabelledReader.setPath(unlabelledPath);
        UnlabelledReader reader = new UnlabelledReader(fileNumber, lineNumber);

        DBN dbn;
        if (args.length == 5 && args[1].equals("continue")) {
            dbn = DBN.load(args[2]);
        } else {
            List<Integer> neurons = Arrays.asList(9, 81, 53, 26);
            dbn = new DBN(neurons);
        }

        String lastPath = null;
        while (reader.hasNext()) {
            int currentFile = reader.getFileNumber();
            int currentLine = reader.getLineNumber();

            dbn.preTrainNetwork(reader.next());
            String path = "" + currentFile + "-" + currentLine + ".txt";
            dbn.save(path);
            if (lastPath != null) {
                boolean deleted = new File(lastPath).delete() && new File(lastPath + "net.txt").delete();
                if (!deleted) {
                    System.out.println("Failed to remove previous network.");
                }
            }

            lastPath = path;
        }
    }
}

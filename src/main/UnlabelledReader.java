package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class UnlabelledReader implements Iterator<List<List<Double>>> {
    private int fileNumber;
    private int lineNumber;

    private static final int firstPosition = 1;
    private static final int lastPosition = 3;
    private static final int fileStart = 0;
    private static final int batchSize = 10000;

    private static final String path = ".\\Data\\Fake\\Unlabelled\\";
    private static final String ext = ".csv";


    public int getFileNumber() {
        return this.fileNumber;
    }

    public UnlabelledReader() {
        this(firstPosition, fileStart);
    }

    public UnlabelledReader(int fileNumber, int lineNumber) {
        this.fileNumber = fileNumber;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean hasNext() {
        return this.fileNumber <= lastPosition;
    }

    @Override
    public List<List<Double>> next() {
        if (!this.hasNext()) {
            throw new IllegalStateException();
        }

        List<List<Double>> res = new ArrayList<>();
        Scanner input;
        try {
            input = new Scanner(new File(path + fileNumber + ext));
            int currentLine = 0;
            while (input.hasNextLine() && res.size() < batchSize) {
                if (currentLine == lineNumber) {
                    String line = input.nextLine();
                    String[] tokens = line.split(",");
                    List<Double> row = new ArrayList<>();
                    for (int i = 1; i < 10; ++i) {
                        row.add(Double.valueOf(tokens[i]));
                    }

                    res.add(row);
                    ++lineNumber;
                } else {
                    input.nextLine();
                }

                ++currentLine;
            }

            if (!input.hasNextLine()) {
                ++fileNumber;
                lineNumber = 0;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return res;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}

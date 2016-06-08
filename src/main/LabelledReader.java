package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class LabelledReader implements Iterator<List<LabelledData>> {
    private int fileNumber;
    private int lineNumber;

    private static final int firstPosition = 1;
    private static final int lastPosition = 1;
    private static final int fileStart = 0;
    private static final int batchSize = 26000;

    private static final String path = ".\\Data\\Fake\\Labelled\\";
    private static final String ext = ".csv";


    public int getFileNumber() {
        return this.fileNumber;
    }

    public LabelledReader() {
        this(firstPosition, fileStart);
    }

    public LabelledReader(int fileNumber, int lineNumber) {
        this.fileNumber = fileNumber;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean hasNext() {
        return this.fileNumber <= lastPosition;
    }

    @Override
    public List<LabelledData> next() {
        if (!this.hasNext()) {
            throw new IllegalStateException();
        }

        List<LabelledData> res = new ArrayList<>();
        Scanner input;
        try {
            input = new Scanner(new File(path + fileNumber + ext));
            int currentLine = 0;
            while (input.hasNextLine() && res.size() < batchSize) {
                if (currentLine == lineNumber) {
                    String line = input.nextLine();
                    String[] tokens = line.split(",");
                    Integer label = ClassMapping.toModel(Integer.valueOf(tokens[0]));
                    List<Double> data = new ArrayList<>();
                    for (int i = 1; i < 10; ++i) {
                        data.add(Double.valueOf(tokens[i]));
                    }

                    res.add(new LabelledData(data, LabelConverter.toList(label, 26)));
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

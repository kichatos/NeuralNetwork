package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class UnlabelledReader implements Iterator<List<List<Double>>> {
    private int fileNumber;
    private int lineNumber;

    private static final int firstPosition = 1;
    private static final int lastPosition = 208;
    private static final int fileStart = 0;
    private static final int batchSize = 10;

    private static String path = "D:\\data\\new_unlabelled\\";
    private static final String ext = ".csv";


    public int getFileNumber() {
        return this.fileNumber;
    }

    public UnlabelledReader() {
        this(firstPosition, fileStart);
    }

    public static void setPath(String path) {
        UnlabelledReader.path= path;
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
            input = new Scanner(new File(path + fileNumber + ext)).useLocale(Locale.US);
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

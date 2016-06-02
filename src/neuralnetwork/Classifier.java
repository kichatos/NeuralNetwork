package neuralnetwork;

import java.util.List;

public interface Classifier {
    int classify(List<Double> input);

    default void testClassifier(List<List<Double>> inputs, List<List<Double>> correctAnswers) {
        if (inputs == null) {
            throw new NullPointerException("Classifier: inputs can't be null");
        }

        if (correctAnswers == null) {
            throw new NullPointerException("Classifier: correctAnswers can't be null");
        }

        if (inputs.size() != correctAnswers.size()) {
            throw new IllegalArgumentException("Classifier: inputs and correctAnswers should be of the same size");
        }

        System.out.println("\n --------- TEST ---------\n");

        int totalInputsCounter = 0;
        int correctAnswersCounter = 0;

        for (int i = 0; i < inputs.size(); ++i) {
            int classID = classify(inputs.get(i));
            int correctClass = 0;

            for (int j = 0; j < correctAnswers.get(i).size(); ++j) {
                if (correctAnswers.get(i).get(j) == 1.0) {
                    correctClass = j;
                }
            }

            System.out.println(inputs.get(i) + " => " + classID + ". Correct answer: " + correctClass);

            if (classID == correctClass) {
                ++correctAnswersCounter;
            }

            ++totalInputsCounter;
        }

        double accuracy =  1.0 * correctAnswersCounter / totalInputsCounter;

        System.out.println("\nAccuracy: " + accuracy);
    }
}

package neuralnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DBN implements Classifier {
    NeuralNetwork network;
    List<RBM> rbms;
    List<Integer> layerSizes;
    Map<Integer, Integer> neuronToClass;
    int numberOfClasses;

    private DBN() {
        rbms = new ArrayList<>();
        layerSizes = new ArrayList<>();
        neuronToClass = new HashMap<>();
    }

    public DBN(List<Integer> layerSizes) {
        this.layerSizes = layerSizes;
        this.network = new NeuralNetwork(layerSizes);

        this.rbms = new ArrayList<>();
        for (int i = 1; i < layerSizes.size(); ++i) {
            rbms.add(new RBM(layerSizes.get(i - 1), layerSizes.get(i)));
        }

        this.numberOfClasses = layerSizes.get(layerSizes.size() - 1);

        this.neuronToClass = new HashMap<>();
        for (int k = 0; k < numberOfClasses; ++k) {
            neuronToClass.put(k, k);
        }
    }

    private void recreateRBMS() {
        rbms.set(0, new RBM(Matrix.fromList(network.getLayerWeights(0)).transpose().toList(), network.getBiases(0)));
        for (int i = 1; i < layerSizes.size() - 1; ++i) {
            rbms.set(i, new RBM(Matrix.fromList(network.getLayerWeights(i)).transpose().toList(), network.getBiases(i), network.getBiases(i - 1)));
        }
    }

    public int classify(List<Double> inputs) {
        return neuronToClass.get(network.classify(inputs));
    }

    public void trainNetwork(List<List<Double>> inputs, List<List<Double>> correctAnswers) {
        preTrainNetwork(inputs);
        updateNeuronToClassMapping(inputs, correctAnswers);
        network.trainNetwork(inputs, mapAnswers(correctAnswers));
    }

    public void preTrainNetwork(List<List<Double>> inputs) {
        rbms.get(0).trainNetwork(inputs);
        NeuralNetwork fullNetwork = new NeuralNetwork(Arrays.asList(layerSizes.get(0), layerSizes.get(1)));
        fullNetwork.setLayerWeights(0, Matrix.fromList(rbms.get(0).getWeights()).transpose().toList());
        fullNetwork.setBiases(0, rbms.get(0).getHiddenBiases());

        for (int i = 1; i < layerSizes.size() - 1; ++i) {
            rbms.get(i).trainNetwork(fullNetwork.getOutputsList(inputs));
            fullNetwork.addLayer(layerSizes.get(i + 1));
            fullNetwork.setLayerWeights(i, Matrix.fromList(rbms.get(i).getWeights()).transpose().toList());
            fullNetwork.setBiases(i - 1, rbms.get(i).getVisibleBiases());
            fullNetwork.setBiases(i, rbms.get(i).getHiddenBiases());
        }

        this.network = fullNetwork;
        recreateRBMS();
    }

    private List<List<Double>> mapAnswers(List<List<Double>> correctAnswers) {
        List<List<Double>> res = new ArrayList<>();
        for (int k = 0; k < correctAnswers.size(); ++k) {
            res.add(new ArrayList<>(Collections.nCopies(numberOfClasses, -1.0)));
        }

        for (int i = 0; i < correctAnswers.size(); ++i) {
            for (int j = 0; j < numberOfClasses; ++j) {
                res.get(i).set(neuronToClass.get(j), correctAnswers.get(i).get(j));
            }
        }

        return res;
    }

    private void updateNeuronToClassMapping(List<List<Double>> inputs, List<List<Double>> correctAnswers) {
        List<List<Integer>> neuronAnswers = new ArrayList<>();
        for (int i = 0; i < numberOfClasses; ++i) {
            neuronAnswers.add(new ArrayList<>(Collections.nCopies(numberOfClasses, 0)));
        }

        for (int i = 0; i < inputs.size(); ++i) {
            Integer ans = correctAnswers.get(i).indexOf(Collections.max(correctAnswers.get(i)));
            Integer neuron = network.classify(inputs.get(i));
            neuronAnswers.get(neuron).set(ans, neuronAnswers.get(neuron).get(ans) + 1);
        }

        for (int k = 0; k < numberOfClasses; ++k) {
            Integer ans = neuronAnswers.get(k).indexOf(Collections.max(neuronAnswers.get(k)));
            for (int j = k + 1; j < numberOfClasses; ++j) {
                neuronAnswers.get(j).set(ans, -1);
            }

            neuronToClass.put(k, ans);
        }
    }

    public void save(String path) {
        try (PrintWriter output = new PrintWriter(new File(path))) {
            output.print(numberOfClasses + " ");
            for (int i = 0; i < numberOfClasses;  ++i) {
                output.print(neuronToClass.get(i) + " ");
            }

            output.println();
            output.print(layerSizes.size() + " ");
            for (int i = 0; i < layerSizes.size(); ++i) {
                output.print(layerSizes.get(i) + " ");
            }

            output.println();
            for (RBM rbm : rbms) {
                output.println(rbm.saveString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            network.saveNetwork(path + "net.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DBN load(String path) {
        DBN res = new DBN();

        try (Scanner input = new Scanner(new File(path)).useLocale(Locale.US)) {
            res.numberOfClasses = input.nextInt();
            for (int i = 0; i < res.numberOfClasses; ++i) {
                res.neuronToClass.put(i, input.nextInt());
            }

            int numberOfLayers = input.nextInt();
            for (int i = 0; i < numberOfLayers; ++i) {
                res.layerSizes.add(input.nextInt());
            }

            input.nextLine();
            for (int i = 1; i < numberOfLayers; ++i) {
                res.rbms.add(RBM.loadString(input.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            res.network = NeuralNetwork.loadNetwork(path + "net.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}

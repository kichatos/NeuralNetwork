package neuralnetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RBM {
    Matrix visHid;
    Matrix hidBiases;
    Matrix visBiases;

    int visibleLayerSize;
    int hiddenLayerSize;

    private double epsilonWeights = 0.001;
    private double epsilonVisibleBiases = 0.001;
    private double epsilonHiddenBiases = 0.001;
    private double maxEpoch = 500;
    private double finalMomentumEpoch = 5;

    private double weightCost = 0.0002;
    private double initialMomentum = 0.5;
    private double finalMomentum = 0.9;

    private void initWeights() {
        visHid = Matrix.random(visibleLayerSize, hiddenLayerSize);
        visBiases = Matrix.zeros(1, visibleLayerSize);
        hidBiases = Matrix.zeros(1, hiddenLayerSize);
    }

    public RBM(int visibleLayerSize, int hiddenLayerSize) {
        if (visibleLayerSize <= 0) {
            throw new IllegalArgumentException("An RBM visible layer size has to be at least one.");
        }

        if (hiddenLayerSize <= 0) {
            throw new IllegalArgumentException("An RBM hidden layer size has to be at least one.");
        }

        this.visibleLayerSize = visibleLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.initWeights();
    }

    public RBM(List<List<Double>> weights) {
        this.visibleLayerSize = weights.size();
        this.hiddenLayerSize = weights.get(0).size();
        this.setWeights(weights);
        this.visBiases = Matrix.zeros(1, visibleLayerSize);
        this.hidBiases = Matrix.zeros(1, hiddenLayerSize);
    }

    public RBM(List<List<Double>> weights, List<Double> hiddenBiases) {
        this(weights);
        this.setHiddenBiases(hiddenBiases);
    }

    public RBM(List<List<Double>> weights, List<Double> hiddenBiases, List<Double> visibleBiases) {
        this(weights, hiddenBiases);
        this.setVisibleBiases(visibleBiases);
    }

    public List<List<Double>> getWeights() {
        return visHid.toList();
    }

    public void setWeights(List<List<Double>> weights) {
        if (weights.size() != visibleLayerSize) {
            throw new IllegalArgumentException("Weights matrix dimensions have to correspond to the number of neurons in the visible layer.");
        }

        for (int i = 0; i < visibleLayerSize; ++i) {
            if (weights.get(i).size() != hiddenLayerSize) {
                throw new IllegalArgumentException("Weights matrix dimensions have to correspond to the number of neurons in the hidden layer.");
            }
        }

        this.visHid = Matrix.fromList(weights);
    }

    public List<Double> getVisibleBiases() {
        return visBiases.toList().get(0);
    }

    public void setVisibleBiases(List<Double> visibleBiases) {
        if (visibleBiases.size() != visibleLayerSize) {
            throw new IllegalArgumentException("Incorrect number of elements in visible biases.");
        }

        List<List<Double>> tmp = new ArrayList<>();
        tmp.add(visibleBiases);
        this.visBiases = Matrix.fromList(tmp);
    }

    public List<Double> getHiddenBiases() {
        return hidBiases.toList().get(0);
    }

    public void setHiddenBiases(List<Double> hiddenBiases) {
        if (hiddenBiases.size() != hiddenLayerSize) {
            throw new IllegalArgumentException("Incorrect number of elements in hidden biases.");
        }

        List<List<Double>> tmp = new ArrayList<>();
        tmp.add(hiddenBiases);
        this.hidBiases = Matrix.fromList(tmp);
    }

    public void trainNetwork(List<List<Double>> dataBatch) {
        Matrix visHidInc = Matrix.zeros(visibleLayerSize, hiddenLayerSize);
        Matrix visBiasInc = Matrix.zeros(1, visibleLayerSize);
        Matrix hidBiasInc = Matrix.zeros(1, hiddenLayerSize);
        int numCases = dataBatch.size();
        Matrix data = Matrix.fromList(dataBatch);
        Matrix ones = Matrix.ones(numCases, visibleLayerSize);

        double momentum = initialMomentum;

        double prevError = 0.0;
        for (int epoch = 0; epoch < maxEpoch; ++epoch) {
            Matrix posHidProbs = data.multiply(visHid).add(hidBiases.rep(numCases));
            Matrix posProds = data.transpose().multiply(posHidProbs);
            Matrix posHidAct = posHidProbs.sum();
            Matrix posVisAct = data.sum();
            Matrix posHidStates = posHidProbs.add(Matrix.random(numCases, hiddenLayerSize));

            Matrix power = posHidStates.multiply(visHid.transpose()).neg().subtract(visBiases.rep(numCases));
            Matrix s = ones.add(power.exp());
            Matrix negData = ones.eDivide(s);
            Matrix negHidProbs = negData.multiply(visHid).add(hidBiases.rep(numCases));
            Matrix negProds = negData.transpose().multiply(negHidProbs);
            Matrix negHidAct = negHidProbs.sum();
            Matrix negVisAct = negData.sum();

            if (epoch >= finalMomentumEpoch) {
                momentum = finalMomentum;
            }

            double error = data.subtract(negData).square().sum().transpose().sum().get(0, 0);
            System.out.println("Epoch " + epoch + ". Error: " + error);

            visHidInc = visHidInc.multiply(momentum).add(posProds.subtract(negProds).subtract(visHid.multiply(weightCost)).multiply(epsilonWeights));
            visBiasInc = visBiasInc.multiply(momentum).add(posVisAct.subtract(negVisAct).multiply(epsilonVisibleBiases));
            hidBiasInc = hidBiasInc.multiply(momentum).add(posHidAct.subtract(negHidAct).multiply(epsilonHiddenBiases));
            visHid = visHid.add(visHidInc);
            visBiases = visBiases.add(visBiasInc);
            hidBiases = hidBiases.add(hidBiasInc);

            if (Math.abs(error - prevError) < 1E-3) {
                break;
            }

            prevError = error;
        }

    }

    public List<Double> getOutput(List<Double> input) {
        List<List<Double>> tmp = new ArrayList<>();
        tmp.add(input);
        Matrix data = Matrix.fromList(tmp);

        Matrix res = data.multiply(visHid).add(hidBiases);
        return res.toList().get(0);
    }

    public String saveString() {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter output = new PrintWriter(stringWriter)) {
            output.print(visibleLayerSize);
            output.print(' ');
            output.print(hiddenLayerSize);
            output.print(' ');

            for (int i = 0; i < visibleLayerSize; ++i) {
                for (int j = 0; j < hiddenLayerSize; ++j) {
                    output.print(visHid.get(i, j));
                    if (!(i == visibleLayerSize && j == hiddenLayerSize)) {
                        output.print(' ');
                    }
                }
            }

            return stringWriter.toString();
        }
        catch (IOException e) {
            return null;
        }
    }

    public void save(String path) {
        try (PrintWriter output = new PrintWriter(new File(path))) {
            output.print(visibleLayerSize);
            output.print(' ');
            output.print(hiddenLayerSize);
            output.println();

            for (int i = 0; i < visibleLayerSize; ++i) {
                for (int j = 0; j < hiddenLayerSize; ++j) {
                    output.print(visHid.get(i, j));
                    output.print(' ');
                }

                output.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RBM load(String path) throws FileNotFoundException {
        try (Scanner input = new Scanner(new File(path))) {
            int visibleLayerSize = input.nextInt();
            int hiddenLayerSize = input.nextInt();

            RBM res = new RBM(visibleLayerSize, hiddenLayerSize);

            for (int i = 0; i < res.visibleLayerSize; ++i) {
                for (int j = 0; j < res.hiddenLayerSize; ++j) {
                    res.visHid.set(i, j, input.nextDouble());
                }
            }

            return res;
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    public static RBM loadString(String string) throws FileNotFoundException {
        try (StringReader stringReader = new StringReader(string);
                Scanner input = new Scanner(stringReader)) {
            int visibleLayerSize = input.nextInt();
            int hiddenLayerSize = input.nextInt();

            RBM res = new RBM(visibleLayerSize, hiddenLayerSize);

            for (int i = 0; i < res.visibleLayerSize; ++i) {
                for (int j = 0; j < res.hiddenLayerSize; ++j) {
                    res.visHid.set(i, j, input.nextDouble());
                }
            }

            return res;
        }
    }
}

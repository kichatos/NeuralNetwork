package neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Matrix {
    double[][] m;
    int height;
    int width;

    private static double MIN_INIT = -0.01;
    private static double MAX_INIT = 0.01;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private Matrix() {

    }

    public Matrix(int height, int width) {
        m = new double[height][width];
        this.height = height;
        this.width = width;
    }

    public Matrix(Matrix a) {
        this(a.height, a.width);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                this.set(i, j, a.get(i, j));
            }
        }
    }

    public static Matrix zeros(int height, int width) {
        return filled(height, width, 0.0);
    }

    public static Matrix ones(int height, int width) {
        return filled(height, width, 1.0);
    }

    public static Matrix filled(int height, int width, double value) {
        Matrix res = new Matrix(height, width);
        res.fill(value);
        return res;
    }

    public static Matrix random(int height, int width) {
        Matrix res = new Matrix(height, width);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < res.height; ++i) {
            for (int j = 0; j < res.width; ++j) {
                res.set(i, j, random.nextDouble(MIN_INIT, MAX_INIT));
            }
        }

        return res;
    }

    public static Matrix fromList(List<List<Double>> list) {
        Matrix res = new Matrix(list.size(), list.get(0).size());
        for (int i = 0; i < res.height; ++i) {
            for (int j = 0; j < res.width; ++j) {
                res.set(i, j, list.get(i).get(j));
            }
        }

        return res;
    }

    public Matrix transpose() {
        return new TransposedMatrix(this);
    }

    public Matrix subMatrix(int i, int j, int mi, int mj) {
        return new SubMatrix(this, i, j, mi, mj);
    }

    public Matrix rep(int count) {
        Matrix res = new Matrix(height * count, width);

        for (int i = 0; i < count; ++i) {
            res.subMatrix(height * i, 0, height * (i + 1), width).set(this);
        }

        return res;
    }

    public Matrix sum() {
        Matrix res = zeros(1, width);
        for (int j = 0; j < width; ++j) {
            for (int i = 0; i < height; ++i) {
                res.add(0, j, this.get(i, j));
            }
        }

        return res;
    }

    public void add(int i, int j, double value) {
        this.set(i, j, this.get(i, j) + value);
    }

    public void subtract(int i, int j, double value) {
        this.set(i, j, this.get(i, j) - value);
    }

    public void multiply(int i, int j, double value) {
        this.set(i, j, this.get(i, j) * value);
    }

    public void divide(int i, int j, double value) {
        this.set(i, j, this.get(i, j) / value);
    }

    public void pow(int i, int j, double power) {
        this.set(i, j, Math.pow(this.get(i, j), power));
    }

    public void square(int i, int j) {
        this.pow(i, j, 2.0);
    }

    public void exp(int i, int j) {
        this.set(i, j, Math.exp(this.get(i, j)));
    }

    public void neg(int i, int j) {
        this.set(i, j, -this.get(i, j));
    }

    public Matrix add(Matrix a) {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.add(i, j, a.get(i, j));
            }
        }

        return res;
    }

    public Matrix subtract(Matrix a) {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.subtract(i, j, a.get(i, j));
            }
        }

        return res;
    }

    public Matrix eMultiply(Matrix a) {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.multiply(i, j, a.get(i, j));
            }
        }

        return res;
    }

    public Matrix multiply(double value) {
        return this.eMultiply(filled(height, width, value));
    }

    public Matrix eDivide(Matrix a) {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.divide(i, j, a.get(i, j));
            }
        }

        return res;
    }

    public Matrix exp() {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.exp(i, j);
            }
        }

        return res;
    }

    public Matrix square() {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.square(i, j);
            }
        }

        return res;
    }

    public Matrix neg() {
        Matrix res = new Matrix(this);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                res.neg(i, j);
            }
        }

        return res;
    }

    public Matrix multiply(Matrix a) {
        if (this.width != a.height) {
            throw new IllegalArgumentException("Illegal matrices dimensions");
        }

        Matrix res = new Matrix(this.height, a.width);
        for (int i = 0; i < res.height; ++i) {
            for (int j = 0; j < res.width; ++j) {
                for (int k = 0; k < this.width; ++k) {
                    res.set(i, j, this.get(i, k) * a.get(k, j));
                }
            }
        }

        return res;
    }

    public void fill(double value) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                this.set(i, j, value);
            }
        }
    }

    public double get(int i, int j) {
        return m[i][j];
    }

    public void set(int i, int j, double value) {
        m[i][j] = value;
    }

    public void set(Matrix a) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                this.set(i, j, a.get(i, j));
            }
        }
    }

    public List<List<Double>> toList() {
        List<List<Double>> res = new ArrayList<>();
        for (int i = 0; i < height; ++i) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < width; ++j) {
                row.add(this.get(i, j));
            }

            res.add(row);
        }

        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("[").append(height).append("; ").append(width).append("]: {");
        for (int i = 0; i < height; ++i) {
            res.append("{");
            for (int j = 0; j < width; ++j) {
                res.append(this.get(i, j));

                if (j != width - 1) {
                    res.append(", ");
                }
            }

            res.append("}");
            if (i != height - 1) {
                res.append(", ");
            }
        }

        res.append("}");
        return res.toString();
    }

    private class TransposedMatrix extends Matrix {
        Matrix origin;

        TransposedMatrix(Matrix origin) {
            this.origin = origin;
            this.height = origin.width;
            this.width = origin.height;
        }

        @Override
        public double get(int i, int j) {
            return origin.get(j, i);
        }

        @Override
        public void set(int i, int j, double value) {
            origin.set(j, i, value);
        }
    }

    private class SubMatrix extends Matrix {
        Matrix origin;
        int ly;
        int lx;
        int ry;
        int rx;

        SubMatrix(Matrix origin, int ly, int lx, int ry, int rx) {
            this.origin = origin;
            this.ly = ly;
            this.lx = lx;
            this.ry = ry;
            this.rx = rx;
            this.width = rx - lx;
            this.height = ry - ly;
        }

        @Override
        public double get(int i, int j) {
            return origin.get(i + ly, j + lx);
        }

        @Override
        public void set(int i, int j, double value) {
            origin.set(i + ly, j + lx, value);
        }
    }
}

package common;

import java.io.Serializable;

public class ModelParameters implements Serializable {
    public double[] weights;
    public String hash;

    public ModelParameters(int size) {
        weights = new double[size];
    }
}
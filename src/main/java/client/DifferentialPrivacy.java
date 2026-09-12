package client;

import java.util.Random;

public class DifferentialPrivacy {

    public static void applyNoise(double[] weights, double epsilon) {
        Random random = new Random();
        double sigma = 1.0 / epsilon;

        for (int i = 0; i < weights.length; i++) {
            weights[i] += random.nextGaussian() * sigma;
        }
    }
}
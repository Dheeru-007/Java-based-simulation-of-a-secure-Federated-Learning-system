package client;

import common.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientNode {

    private static final String SERVER = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) throws Exception {

        List<double[]> dataset = CSVReaderUtil.readCSV();

        int featureCount = 8;
        double[] weights = new double[featureCount];

        // Simple gradient-style weight estimation
        for (double[] row : dataset) {

            double y = row[8]; // risk_of_diabetes (label)

            for (int i = 0; i < featureCount; i++) {
                weights[i] += row[i] * y;
            }
        }

        for (int i = 0; i < featureCount; i++) {
            weights[i] /= dataset.size();
        }

        ModelParameters model = new ModelParameters(featureCount);
        model.weights = weights;

        System.out.println("Local trained weights:");
        for (double w : weights) {
            System.out.print(w + " ");
        }
        System.out.println();

        // Differential Privacy
        DifferentialPrivacy.applyNoise(model.weights, 0.5);

        // SHA-256
        model.hash = SHAUtil.sha256(serialize(model.weights));

        // AES Encrypt
        byte[] encrypted = AESUtil.encrypt(serialize(model));

        // Send to server
        Socket socket = new Socket(SERVER, PORT);
        ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());

        out.writeObject(encrypted);
        socket.close();

        System.out.println("Client update sent.");
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        return bos.toByteArray();
    }
}
package server;

import common.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class AggregationServer {

    private static final int PORT = 5000;
    private static final int CLIENTS_REQUIRED = 2;
    private static final int MODEL_SIZE = 8;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Federated Server Started...");

        List<ModelParameters> updates = new ArrayList<>();

        while (updates.size() < CLIENTS_REQUIRED) {

            Socket socket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Receive object
            Object received = in.readObject();

            // Validate type (detect malicious client)
            if (!(received instanceof byte[])) {
                System.out.println("❌ Malicious or invalid update detected. Rejected.");
                socket.close();
                continue;
            }

            byte[] encryptedData = (byte[]) received;

            // Decrypt update
            byte[] decryptedData = AESUtil.decrypt(encryptedData);

            ObjectInputStream ois =
                    new ObjectInputStream(new ByteArrayInputStream(decryptedData));

            ModelParameters model = (ModelParameters) ois.readObject();

            // Validate model size
            if (model.weights.length != MODEL_SIZE) {
                System.out.println("❌ Invalid model size. Rejected.");
                socket.close();
                continue;
            }

            // SHA verification
            String recomputedHash = SHAUtil.sha256(serialize(model.weights));
            if (!recomputedHash.equals(model.hash)) {
                System.out.println("❌ Hash mismatch. Update rejected.");
                socket.close();
                continue;
            }

            System.out.println("✔ Update accepted.");
            System.out.println("Received weights: " + Arrays.toString(model.weights));

            updates.add(model);
            socket.close();
        }

        // Federated Averaging
        double[] global = new double[MODEL_SIZE];

        for (int i = 0; i < MODEL_SIZE; i++) {
            for (ModelParameters m : updates) {
                global[i] += m.weights[i];
            }
            global[i] /= updates.size();
        }

        System.out.println("\nGlobal Model:");
        System.out.println(Arrays.toString(global));

        System.out.println("Accuracy: " + calculateAccuracy(global) + "%");

        serverSocket.close();
    }

    private static double calculateAccuracy(double[] weights) {
        double sum = 0;

        for (double w : weights)
            sum += Math.abs(w);

        return Math.max(0, 100 - sum);
    }

    private static byte[] serialize(Object obj) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(obj);
        oos.flush();

        return bos.toByteArray();
    }
}
package client;

import common.ModelParameters;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class MaliciousClient {

    private static final String SERVER = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) throws Exception {

        ModelParameters model = new ModelParameters(5);
        Random random = new Random();

        // Poisoned / fake update
        for (int i = 0; i < model.weights.length; i++) {
            model.weights[i] = random.nextDouble() * 1000;
        }

        model.hash = "fake_hash";

        Socket socket = new Socket(SERVER, PORT);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(model);
        socket.close();

        System.out.println("Malicious update sent.");
    }
}
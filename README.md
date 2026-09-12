# Secure Federated Learning Simulation

This is a Java-based simulation of a **Secure Federated Learning** system. It demonstrates how multiple clients can train a machine learning model locally on their own data and send their model updates to a central server without sharing the raw data.

The system is designed to simulate predicting diabetes risk from local client data, and it heavily focuses on the **security and privacy** aspects of federated learning.

## Features

* **Federated Averaging**: The server aggregates model weights from multiple clients to produce a global model.
* **Differential Privacy**: Clients inject statistical noise (Gaussian mechanism) into their trained weights to obscure exact values and prevent reverse-engineering of individual data points.
* **AES Encryption**: Model updates are encrypted in transit over the network.
* **SHA-256 Hashing**: Hash signatures guarantee the integrity of the model updates and prevent tampering.
* **Malicious Actor Defense**: The server implements strict validations (payload decryption, hash verification, shape validation) to reject poisoned or invalid updates from malicious clients.
* **Local Data Ingestion**: Parses tab-separated values (TSV/CSV) to extract features and labels (ignoring raw client IDs).

## Architecture

1. **Client (`client.ClientNode`)**:
   - Reads local dataset (`client1.csv`).
   - Performs basic gradient-style local training.
   - Applies Differential Privacy, calculates a SHA-256 hash, and encrypts the payload using AES.
   - Sends the update to the server over a TCP socket.
2. **Server (`server.AggregationServer`)**:
   - Listens for incoming connections on port 5000.
   - Expects a strict threshold of clients (currently 2).
   - Validates, decrypts, and verifies incoming updates.
   - Aggregates the weights into a global model.
3. **Attacker (`client.MaliciousClient`)**:
   - Simulates a bad actor attempting to poison the network with fake/unencrypted updates to test the server's defenses.

## Prerequisites

* **Java Development Kit (JDK) 25** or higher
* **Apache Maven**

*(Note: The project is strictly configured to use Java 25 compiler compliance.)*

## How to Run

1. **Compile the Project**
   Open your terminal in the project root directory and run:
   ```bash
   mvn clean compile
   ```

2. **Start the Aggregation Server**
   In a new terminal window, start the server so it can listen for incoming client updates:
   ```bash
   java -cp target/classes server.AggregationServer
   ```

3. **Start the Clients**
   Because the server requires 2 clients to perform aggregation, open two separate terminal windows and run the client in both:
   ```bash
   # Terminal 2 (Client 1)
   java -cp target/classes client.ClientNode
   
   # Terminal 3 (Client 2)
   java -cp target/classes client.ClientNode
   ```

4. **(Optional) Test Defenses with Malicious Client**
   Run the malicious client to observe how the server blocks invalid updates:
   ```bash
   java -cp target/classes client.MaliciousClient
   ```

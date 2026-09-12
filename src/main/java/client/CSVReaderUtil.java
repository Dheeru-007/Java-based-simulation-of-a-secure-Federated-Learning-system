package client;

import java.io.*;
import java.util.*;

public class CSVReaderUtil {

    public static List<double[]> readCSV() throws Exception {

        List<double[]> data = new ArrayList<>();

        InputStream inputStream =
                CSVReaderUtil.class
                        .getClassLoader()
                        .getResourceAsStream("data/client1.csv");

        if (inputStream == null) {
            throw new FileNotFoundException("CSV file not found");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        // Skip header
        br.readLine();

        while ((line = br.readLine()) != null) {

            String[] values = line.split("\\t");

            double[] row = new double[9];
            // 8 features + 1 label

            // Skip column 0 (client_id)
            for (int i = 1; i <= 9; i++) {
                row[i - 1] = Double.parseDouble(values[i].trim());
            }

            data.add(row);
        }

        br.close();
        return data;
    }
}
package com.berghella.daniele.edu_hub.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ApiCall {
    public static void ciao()
            throws URISyntaxException
    {

        // API URL
        String url = "http://localhost:3000/users";

        // JSON String which will be sent to the API.
        String data_to_send = "{\n" +
                "      \"id\": \"95cs\",\n" +
                "      \"name\": \"Daniele Berghella2\",\n" +
                "      \"username\": \"Danbe922\",\n" +
                "      \"email\": \"daniele.berghella22@gmail.com\"\n" +
                "    }";

        try {
            URL obj = new URI(url).toURL(); // Making an object to point to the API URL

            // attempts to establish a connection to the URL represented by the obj.
            HttpURLConnection connection = (HttpURLConnection)obj.openConnection();

            // Set request method and enable writing to the connection
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set content type header,
            // input (Content-Type) is in JSON (application/json) format.
            connection.setRequestProperty("Content-Type", "application/json");

            // Calling the API and send request data
            // connection.getOutputStream() purpose is to obtain an output stream for sending data to the server.
            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.writeBytes(data_to_send);
                os.flush();
            }

            // Get response code and handle response
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) {
                // HTTP_OK or 200 response code generally means that the server ran successfully without any errors
                StringBuilder response = new StringBuilder();

                // Read response content
                // connection.getInputStream() purpose is to obtain an input stream for reading the server's response.
                try (
                        BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line); // Adds every line to response till the end of file.
                    }
                }
                System.out.println("Response: " + response);
            }
            else {
                System.out.println("Error: HTTP Response code - " + responseCode);
            }
            connection.disconnect();
        }
        catch (IOException e) {
            // If any error occurs during api call it will go into catch block
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

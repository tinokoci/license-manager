package dev.strongtino.soteria;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Stream;

public class ImplementationExample {

    public static void main(String[] args) {
        if (isLicenseValid("C5NPG5M922GLPG0J6NM77GKVN7TC8IEH", "Soteria")) {
            // Load the application...
        } else {
            System.exit(1);
        }
    }

    private static boolean isLicenseValid(String license, String software) {
        try {
            URL url = new URL("http://localhost:8080/license?key=" + license + "&software=" + software);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");

            // Too Many Requests
            if (connection.getResponseCode() == 429) {
                respond("You've sent too many license requests.", "Please wait a few minutes and try again.");
                return false;
            }
            InputStream response = connection.getInputStream();
            JsonObject object = JsonParser.parseReader(new InputStreamReader(response, StandardCharsets.UTF_8)).getAsJsonObject();

            // Just in case
            if (object == null) {
                respond("Something went wrong while trying to validate the software.", "Please try again or contact the software distributor.");
                return false;
            }
            boolean valid = object.get("validationType").getAsString().equals("VALID");
            String user = object.get("user").isJsonNull() ? null : object.get("user").getAsString();

            if (!valid || user == null) {
                respond("License key is invalid. If you think that's a", "mistake please contact the software distributor.");
                return false;
            }
            respond("Hello " + user + ", thanks for purchasing " + software + '!');
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            respond("Something went wrong while trying to validate the software.", "Please try again or contact the software distributor.");
            return false;
        }
    }

    private static void respond(String... text) {
        String line = String.join("", Collections.nCopies(50, "#"));

        System.out.println(line);
        Stream.of(text).forEach(System.out::println);
        System.out.println(line);
    }
}

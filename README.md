# license-manager

This is a system that allows you to manage licenses for various software. 
  
The backend is built using spring boot and has one REST GET endpoint for retrieving license information while frontend is built using the java discord api (JDA).

### Usage
You need to have a server that will be running the backend 24/7 so it can respond to the HTTP requests and you need to make an implementation that will fire that request when your application starts.
  
A simple implementation example can be seen below: 

```java
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
```

### Warning
You need to make sure to properly obfuscate your software because otherwise the licensing can be easily removed with a decompiler.

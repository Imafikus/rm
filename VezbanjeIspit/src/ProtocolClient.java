import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ProtocolClient {

    public static void main(String[] args) {
        try {
            URL url = new URL(null, "daytime://localhost:12345?q=test", new ProtocolHandler());


            var conn = url.openConnection();
            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Server response: " + line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

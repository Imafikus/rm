import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ProtocolServer {

    private static class ServerWorker implements Runnable {
        Socket client;

        public ServerWorker(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {

                String line = in.readLine();
                if (line == null) {
                    return;
                }

                System.out.println("Received from client: " + line);

                while (true) {
                    var date = new Date();
                    out.write(date.toString());
                    out.newLine();
                    out.flush();
                    TimeUnit.SECONDS.sleep(5);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(12345)) {
            while (true) {
                new Thread(new ServerWorker(server.accept())).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

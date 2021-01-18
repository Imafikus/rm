import java.io.*;
import java.net.Socket;

public class TCPClient {

    private static boolean bye = false;

    private static class ClientWriter implements Runnable {
        Socket client;

        public ClientWriter(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try(BufferedReader stdio = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {

                while(!bye) {
                    var line = stdio.readLine();
                    bye = line.equals("bye");

                    out.write(line);
                    out.newLine();
                    out.flush();
                }

            } catch (IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientReader implements Runnable {
        Socket client;

        public ClientReader(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                while(!bye) {
                    var line = in.readLine();

                    if(line == null) {
                        break;
                    }

                    System.out.println("From server: " + line);
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        try(Socket client = new Socket("localhost", 12345)) {
            var reader = new Thread(new ClientReader(client));
            var writer = new Thread(new ClientWriter(client));

            reader.start();
            writer.start();

            reader.join();
            writer.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

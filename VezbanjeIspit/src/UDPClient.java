import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {

    public static void main(String[] args) {
        try(DatagramSocket client = new DatagramSocket()) {

            Scanner sc = new Scanner(System.in);
            var n = sc.nextLine().getBytes();


            var packet = new DatagramPacket(n, n.length, InetAddress.getByName("localhost"), 12345);
            client.send(packet);

            while (true) {

                var response = new DatagramPacket(new byte[8], 8);
                client.receive(response);

                var resString = new String(response.getData(), 0, response.getLength());

                if(resString.equals("bye")) {
                    break;
                }

                System.out.println("Received from server: " + resString);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

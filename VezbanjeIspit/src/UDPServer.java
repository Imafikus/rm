import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class UDPServer {

    private static ArrayList<String> allStrings = new ArrayList<>();

    public static void main(String[] args) {
        try(DatagramSocket server = new DatagramSocket(12345)) {
            while (true) {
                try {
                    DatagramPacket req = new DatagramPacket(new byte[8], 8);
                    server.receive(req);

                    var reqString = new String(req.getData(), 0, req.getLength());
                    System.out.println("Received: " + reqString);

                    allStrings.add(reqString);

                    for(var s : allStrings) {
                        var res = s.getBytes();

                        DatagramPacket packet = new DatagramPacket(res, res.length, req.getAddress(), req.getPort());
                        server.send(packet);
                    }

                    var endStr = "bye".getBytes();
                    var endPacket = new DatagramPacket(endStr, endStr.length, req.getAddress(), req.getPort());
                    server.send(endPacket);

                } catch (IOException e) {
                    e.printStackTrace();
            }
}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

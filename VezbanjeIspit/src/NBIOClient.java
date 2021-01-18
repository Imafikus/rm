import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NBIOClient {


    public static void main(String[] args) {
        try(SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 12345))) {

            Scanner sc = new Scanner(System.in);
            var buf = ByteBuffer.allocate(512);

            while (true) {
                var line = sc.nextLine();
                buf.put(line.getBytes());
                buf.put((byte)'\n');
                buf.flip();

                client.write(buf);
                buf.clear();



                client.read(buf);
                var received = new String(buf.array(), 0, buf.position());
                System.out.println("Received from server: " + received);

                buf.clear();
            }



        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
}

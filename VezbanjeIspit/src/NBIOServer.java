import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class NBIOServer {

    private static HashMap<SelectionKey, String> mappings = new HashMap<>();

    public static void main(String[] args) {
        try(ServerSocketChannel serverChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
        ) {
            serverChannel.bind(new InetSocketAddress(12345));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {

                selector.select();
                var it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    var key = it.next();
                    it.remove();
                    try {
                        if(key.isAcceptable()) {
                            System.out.println("isAcceptable");

                            var server = (ServerSocketChannel)key.channel();

                            var client = server.accept();
                            client.configureBlocking(false);

                            var clientKey = client.register(selector, SelectionKey.OP_READ);
                            clientKey.attach(ByteBuffer.allocate(512));
                            System.out.println("Received client");
                        }

                        else if (key.isReadable()) {
                            System.out.println("isReadable");
                            var client = (SocketChannel)key.channel();
                            var buf = (ByteBuffer)key.attachment();

                            client.read(buf);
                            var received = new String(buf.array(), 0, buf.position());
                            if(!buf.hasRemaining() || received.contains("\n")) {

                                System.out.println("Received from client: " + received);
                                mappings.put(key, received);

                                String forSend = mappings.get(key);
                                key.interestOps(SelectionKey.OP_WRITE);
                                buf.clear();
                                buf.put(forSend.getBytes());
                                buf.flip();
                            }
                        }

                        else if (key.isWritable()) {
                            System.out.println("isWritable");
                            var client = (SocketChannel)key.channel();
                            var buf = (ByteBuffer)key.attachment();

                            client.write(buf);
                            if(!buf.hasRemaining()) {
                                key.interestOps(SelectionKey.OP_READ);
                                buf.clear();
                            }

                        }

                    } catch (IOException e) {
                        key.cancel();
                        key.channel().close();
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

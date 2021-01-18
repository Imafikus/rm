import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ProtocolHandler extends URLStreamHandler {


    @Override
    protected int getDefaultPort() {
        return 12345;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new ProtocolConnection(u);
    }
}

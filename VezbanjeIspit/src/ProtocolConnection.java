import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ProtocolConnection extends URLConnection {

    Socket connection = null;

    protected ProtocolConnection(URL url) {
        super(url);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.connected) {
            this.connect();
        }
        return this.connection.getInputStream();
    }

    @Override
    public synchronized void connect() throws IOException {
        if(!this.connected) {
            int port = this.url.getPort();
            if(port < 1 || port > 65535) {
                port = this.url.getDefaultPort();
            }
            this.connection = new Socket(this.url.getHost(), port);
            var query = this.url.getQuery() + "\n";
            OutputStream out = this.connection.getOutputStream();
            out.write(query.getBytes());
            out.flush();
        }
    }
}

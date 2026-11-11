import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * HTTP is only TEXT sent over a network connection. This program has no web library at all: a client and a "server" are
 * two plain sockets on this computer. It prints exactly what travels in each direction.
 *
 * Run:  java RawHttp.java
 */
public class RawHttp {

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) { // port 0: any free port
            Thread server = new Thread(() -> serveOneRequest(listener));
            server.start();

            String request = "GET /health HTTP/1.1\r\n"   // request line: method, target, version
                    + "Host: localhost\r\n"               // headers: Name: value
                    + "Connection: close\r\n"
                    + "\r\n";                              // an EMPTY line ends the headers

            try (Socket client = new Socket(InetAddress.getLoopbackAddress(), listener.getLocalPort())) {
                client.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
                client.getOutputStream().flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                System.out.println("--- the client sent ---");
                System.out.print(show(request));
                System.out.println("--- the server answered ---");
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            }
            server.join();
        }
    }

    /** The "server": reads the request lines up to the empty line, then writes a fixed answer. */
    private static void serveOneRequest(ServerSocket listener) {
        try (Socket connection = listener.accept()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String requestLine = in.readLine();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // headers are read and ignored here
            }
            String body = requestLine.startsWith("GET /health ") ? "OK" : "Not found";
            int status = body.equals("OK") ? 200 : 404;
            String reply = "HTTP/1.1 " + status + (status == 200 ? " OK" : " Not Found") + "\r\n"
                    + "Content-Type: text/plain; charset=utf-8\r\n"
                    + "Content-Length: " + body.length() + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n"
                    + body;
            OutputStream out = connection.getOutputStream();
            out.write(reply.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            System.out.println("server problem: " + e.getMessage());
        }
    }

    /** Makes the invisible line endings visible, so you can see where each line stops. */
    private static String show(String text) {
        return text.replace("\r\n", "<CRLF>\n");
    }
}

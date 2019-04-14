package channel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class TCPClient {

    private Socket socket;

    public TCPClient(InetAddress addr, int port) {
        try {
            socket = new Socket(addr, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) throws IOException {
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        dOut.writeInt(data.length);
        dOut.write(data);
    }
}
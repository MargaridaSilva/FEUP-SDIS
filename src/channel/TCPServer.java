package channel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.MessageHandler;

public class TCPServer implements Runnable {

    private ServerSocket server_socket;
    private Socket socket;
    private Boolean run;

    public TCPServer(int port) {
        try {
            server_socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.run = false;
    }

    public void startReceive(){
        this.run = true;
        ThreadPool.executor.execute(this);
    }

    public void stopReceive() {
        this.run = false;
    }

    public void close(){
        try {
            server_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        
        while (this.run) {
            try {
                socket = server_socket.accept();
        
                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                int length = dIn.readInt();
                if(length>0) {
                    byte[] buf = new byte[length];
                    dIn.readFully(buf, 0, buf.length);
                    MessageHandler task = new MessageHandler(buf, socket.getInetAddress());
                    ThreadPool.executor.execute(task);
                }

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import protocol.Message;
import utilities.Utilities;

public class Channel implements Runnable{

    // private ChannelListener listener;
    private InetAddress inet_addr;
    private int port;
    private MulticastSocket socket;

    public Channel(String addr, int port) throws IOException {
        this.inet_addr = InetAddress.getByName(addr);
        this.port = port;
        this.socket = new MulticastSocket(this.port);
        this.socket.joinGroup(this.inet_addr);
        // this.listener = new ChannelListener();
    }


    public void startReceive() throws IOException {
        Executor e = Executors.newSingleThreadExecutor();
        e.execute(this);
    }


    public void sendMessage(Message message) throws IOException {
        DatagramPacket packet = new DatagramPacket(message.buf, message.buf_len, this.inet_addr, this.port);
        this.socket.send(packet);

    }

    @Override
    public void run() {
        System.out.println("Listening...");
        byte[] buf = new byte[Utilities.UDP_MAX];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        try {
            while(true){
                System.out.println("Before Receive");
                socket.receive(recv);

                System.out.println("After Receive");
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
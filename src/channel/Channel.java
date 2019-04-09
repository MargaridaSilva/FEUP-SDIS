package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
        e.execute(mdbTask);
    }


    public void sendMessage(Message message){


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
                Message message = new Message(recv);

                processMessage(message);
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
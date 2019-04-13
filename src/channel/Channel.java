package channel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocol.MessageHandler;
import protocol.ProtocolMessage;
import utilities.Utilities;

public class Channel implements Runnable {

    private InetAddress inet_addr;
    private int port;
    private MulticastSocket socket;
    private ExecutorService e;

    public Channel(String addr, int port) throws IOException {
        this.inet_addr = InetAddress.getByName(addr);
        this.port = port;
        this.socket = new MulticastSocket(this.port);
        this.socket.joinGroup(this.inet_addr);
    }

    public void startReceive() throws IOException {
        this.e = Executors.newSingleThreadExecutor();
        this.e.execute(this);
    }

    public void stopReceive() throws IOException {
        this.e.shutdown();
        this.socket.leaveGroup(this.inet_addr);
    }

    public void sendMessage(ProtocolMessage message) throws IOException {
        System.out.println("Message sent");
        message.printMessageInfo();
        System.out.println("");
        DatagramPacket packet = new DatagramPacket(message.buf, message.buf_len, this.inet_addr, this.port);
        this.socket.send(packet);
    }

    @Override
    public void run() {
        byte[] buf = new byte[Utilities.UDP_MAX];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        while(!Thread.interrupted()){
            try {
                socket.receive(recv);
                byte[] buf_copy = Arrays.copyOf(recv.getData(),recv.getLength());
                MessageHandler task = new MessageHandler(buf_copy);
                ThreadPool.executor.execute(task);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
    }

}
package channel;

import java.net.DatagramPacket;

import utilities.Utilities;

class ChannelListener implements Runnable {


    public ChannelListener(){

    }
    
    @Override
    public void run() {
        System.out.println("Listening...");
        byte[] buf = new byte[Utilities.UDP_MAX];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        try {
            while(true){
            
                System.out.println("Before Receive");
                mdb.receive(recv);

                System.out.println("After Receive");
                Message message = new Message(recv);

                processMessage(message);
        }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    
}
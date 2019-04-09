package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import utilities.Utilities;

class Message {
    String version, file_id;
    int sender_id, chunk_num, replication;
    String message, type;
    byte[] buf, body;
    int buf_len, body_len;

    public static final String FINAL_SEQ = "\r\n\r\n";

    public Message(DatagramPacket packet) {
        buf = packet.getData();
        buf_len = packet.getLength();
        System.out.println("Message1");
        System.out.println(buf_len);
        String msg = new String(buf);
        String header = "";
        ArrayList<String> arr = new ArrayList<String>();

        // REFACTOR: WHILE MESSAGE NOT EMPTY

        // for (byte ch: buf) {
        // char c = (char) ch;  
        // if (c=='\r'){
        // if (this.isEmptyHeader(header)){
        // break;
        // }
        // else{
        // arr.add(header);
        // }
        // }
        // header += c;
        // }

        int index = Utilities.indexSeq(buf, FINAL_SEQ.getBytes());

        if (index != -1) {
            byte[] header_bytes = Arrays.copyOfRange(buf, 0, index - 1);
            header = new String(header_bytes);
            body = Arrays.copyOfRange(buf, index + FINAL_SEQ.length(), buf_len);
            body_len = body.length;
        }
        else{
            //Throw Error
        }

        String[] args = header.split(" ");
        this.type = args[0];
        this.version = args[1];
        this.sender_id = Integer.parseInt(args[2]);
        this.file_id = args[3];
        this.chunk_num = Integer.parseInt(args[4]);
        this.replication = Integer.parseInt(args[5]);

        // message = new String(buf);

        this.printMessageInfo();
    }

    public Message(String version, int sender_id, String file_id, int chunk_num, int replication, byte[] body, int body_len) {

        String header = String.join(" ", "PUTCHUNK", version, String.valueOf(sender_id), file_id, String.valueOf(chunk_num),
                String.valueOf(replication), FINAL_SEQ);
        
        byte[] header_bytes = header.getBytes();


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(header_bytes);
            outputStream.write(body, 0, body_len);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buf = outputStream.toByteArray();
        buf_len = buf.length;
    }

    public String toString() {
        return message;
    }
    
    public boolean isEmptyHeader(String header) {
    	return header == "";
    }
    
    public void printMessageInfo() {
    	System.out.println("Type: "+ this.type);
    	System.out.println("Sender: "+this.sender_id);
    	System.out.println("File: "+ this.file_id);
    }

}
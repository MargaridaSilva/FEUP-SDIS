package protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import utilities.Utilities;

public class ProtocolMessage {
	public static String version = "1.0";
    public static enum Type { PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED};
    
    public String file_id, message;
    public int sender_id, chunk_num, replication, buf_len, body_len;
    public byte[] buf, body;
	public ProtocolMessage.Type type;

    public static final String FINAL_SEQ = "\r\n\r\n";

    public ProtocolMessage(byte[] packet) {

        buf = packet;
        buf_len = packet.length;
        String header = "";

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

        this.processHeader(header);
    }

    public ProtocolMessage(String sub_protocol, int sender_id, String file_id, int chunk_num, int replication, byte[] body, int body_len) {

        String header = String.join(" ", "PUTCHUNK", ProtocolMessage.version, String.valueOf(sender_id), file_id, String.valueOf(chunk_num),
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
    
    public void processHeader(String header) {
    	String[] args = header.split(" ");
    	this.sender_id = Integer.parseInt(args[2]);
    	this.file_id = args[3];
        switch(args[0]) {
		 case "PUTCHUNK":
			 this.type = Type.PUTCHUNK;
			 break;
		 case "STORED":
			 this.type = Type.STORED;
			 break;
		 case "GETCHUNK":
			 this.type = Type.GETCHUNK;
			 break;
		 case "CHUNK":
			 this.type = Type.CHUNK;
			 break;
		 case "DELETE":
			 this.type = Type.DELETE;
			 break;
		 case "REMOVED":
			 this.type = Type.REMOVED;
			 break;
		default:
			System.out.println("INVALID MESSAGE TYPE");
		 }
        if (this.type != Type.DELETE) {
        	this.chunk_num = Integer.parseInt(args[4]);
        	if (this.type == Type.PUTCHUNK)
        		this.replication = Integer.parseInt(args[5]);
        }
    }

}
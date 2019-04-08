package server;

import java.net.DatagramPacket;
import java.util.ArrayList;

class Message {
    String version, body;
    int sender_id, file_id, chunk_num, replication;
    String message, type;
    byte[] buf;
    int buf_len;

    public Message(DatagramPacket packet){
        buf = packet.getData();
        buf_len = buf.length;
        String msg = new String(buf);
        String header = "";
        ArrayList<String> arr = new ArrayList<String>();
        
        // REFACTOR: WHILE MESSAGE NOT EMPTY
        
        for (byte ch: buf) {
        	char c = (char) ch;
        	if (c=='\r')
        		if (this.isEmptyHeader(header))
        			break;
        		else arr.add(header);
        	header += c;
        }
        
        String[] args = header.split(" "); 
        this.type = args[0];
        this.version = args[1];
        this.sender_id = Integer.parseInt(args[2]);
        this.file_id = Integer.parseInt(args[3]);
        this.chunk_num = Integer.parseInt(args[4]);
        this.replication = Integer.parseInt(args[5]);
        
        message = new String(buf);
        
        this.printMessageInfo();
        //missing body
    }
    
    public Message(String version, int sender_id, int file_id, int chunk_num, int replication, byte[] body){
    	String header = String.join(" ", "PUTCHUNK", version, String.valueOf(sender_id), String.valueOf(file_id), String.valueOf(chunk_num), String.valueOf(replication), "\r\n\r\n", new String(body)); 
        message = header + body;
        buf = message.getBytes();
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
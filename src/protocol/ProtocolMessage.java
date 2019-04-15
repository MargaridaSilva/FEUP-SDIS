package protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import utilities.Utilities;

public class ProtocolMessage {
    public static enum Type { PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED, LEASE, LEASED};
    private static final String FINAL_SEQ = "\r\n\r\n";
    
    public String file_id;
    public int sender_id, chunk_num, replication, buf_len, body_len;
    public byte[] buf, body;
    public ProtocolMessage.Type type;
	public String version;


    public ProtocolMessage(byte[] packet) throws Exception {

        this.buf = packet;
        this.buf_len = packet.length;

        int index = Utilities.indexSeq(buf, FINAL_SEQ.getBytes());
        if (index == -1) {
            throw new Exception();
        }

        this.body = Arrays.copyOfRange(buf, index + FINAL_SEQ.length(), this.buf_len);
        this.body_len = body.length;
        
        byte[] header_bytes = Arrays.copyOfRange(buf, 0, index - 1);
        String header = new String(header_bytes);
        String[] args = header.split(" ");

    	this.sender_id = Integer.parseInt(args[2]);
    	this.file_id = args[3];
    	this.version = args[1];
        this.type = getType(args[0]);
        this.chunk_num = Integer.parseInt(args[4]);
        this.replication = Integer.parseInt(args[5]);
    }

    public ProtocolMessage(String sub_protocol, String version, int sender_id, String file_id, int chunk_num, int replication, byte[] body, int body_len) {

        this.file_id = file_id;
        this.sender_id = sender_id;
        this.chunk_num = chunk_num;
        this.replication = replication;
        this.body = body;
        this.body_len = body_len;
        this.type = getType(sub_protocol);
    
        String header = String.join(" ", sub_protocol, version, String.valueOf(sender_id), file_id, String.valueOf(chunk_num),
                String.valueOf(replication), FINAL_SEQ);
        
        byte[] header_bytes = header.getBytes();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            if(header_bytes != null){
                outputStream.write(header_bytes);
            }
            if(body != null){
                outputStream.write(body, 0, body_len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.buf = outputStream.toByteArray();
        this.buf_len = buf.length;
    }
    
    
    public boolean isEmptyHeader(String header) {
    	return header == "";
    }
    
    public void printMessageInfo() {
        System.out.println( "Type: "+ this.type + "\n" +
                            "Sender: "+this.sender_id + "\n" +
                            "File: "+ this.file_id + "\n" +
                            "Chunk no.: " + this.chunk_num);
    }
    
    public Type getType(String type){
        switch(type) {
            case "PUTCHUNK":
                return Type.PUTCHUNK;
            case "STORED":
                return Type.STORED;
            case "GETCHUNK":
                return Type.GETCHUNK;
            case "CHUNK":
                return  Type.CHUNK;
            case "DELETE":
                return Type.DELETE;
            case "REMOVED":
                return Type.REMOVED;
            case "LEASE":
            	return Type.LEASE;
            case "LEASED":
            	return Type.LEASED;
           default:
               return null;
            }
    }

    public void processHeader(String header) {
    	String[] args = header.split(" ");
    	this.sender_id = Integer.parseInt(args[2]);
    	this.file_id = args[3];
    	this.version = args[1];
        this.type = getType(args[0]);
        if (this.type != Type.DELETE) {
        	this.chunk_num = Integer.parseInt(args[4]);
        	if (this.type == Type.PUTCHUNK)
        		this.replication = Integer.parseInt(args[5]);
        }
    }

}
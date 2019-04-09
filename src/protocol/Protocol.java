package protocol;

import java.io.IOException;

import server.ServerInfo;

public class Protocol {

    private static ServerInfo server = ServerInfo.getInstance();

    public static void putchunk(String file_id, int chunk_num, int replication, byte[] bytes, int readBytes) throws IOException {
        ProtocolMessage message = new ProtocolMessage("PUTCHUNK", server.server_id, file_id, chunk_num, replication, bytes, readBytes);
        server.mdb.sendMessage(message);
    }
    
    public static void stored(String file_id, int chunk_num) throws IOException {
    	ProtocolMessage message = new ProtocolMessage("STORED", server.server_id, file_id, chunk_num, 0, null, 0);
        server.mc.sendMessage(message);
    }
}
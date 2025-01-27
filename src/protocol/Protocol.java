package protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import channel.TCPClient;
import state.ChunkId;
import state.ServerState;
import utilities.Utilities;
import server.ServerInfo;

public class Protocol {

    private static final int MAX_TRIES = 5;
    private static final int BASE_WAIT_TIME_MS = 1000;
    private static final int STORED_MAX_DELAY_MS = 400;
    private static final int CHUNK_MAX_DELAY_MS = 400;
    private static final int PUTCHUNK_MAX_DELAY_MS = 400;

    private static ServerInfo server = ServerInfo.getInstance();

    public static void putchunk(String version, ChunkId chunk_id, int replication, byte[] bytes, int readBytes) throws IOException {

        ProtocolMessage message = new ProtocolMessage("PUTCHUNK", version, server.server_id,
                chunk_id.file_id, chunk_id.chunk_no, replication, bytes, readBytes);

        int tries = 0;
        int perceived_replication;
        int wait_time = BASE_WAIT_TIME_MS;

        do {
            System.out.println("Try no. " + (tries + 1));

            server.mdb.sendMessage(message);
            try {
                System.out.println("Going to sleep for " + wait_time + " ms\n");
                Thread.sleep(wait_time);
                System.out.println("After sleep " + wait_time + " ms\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            perceived_replication = ServerState.get_perceived_replication(chunk_id);

            tries++;
            wait_time *= 2;
        } while (tries < MAX_TRIES && perceived_replication < replication);
    }

    public static void stored(ChunkId chunk_id) throws IOException {
        ProtocolMessage message = new ProtocolMessage("STORED", server.protocol_ver, server.server_id, chunk_id.file_id,
                chunk_id.chunk_no, 0, null, 0);
        Random rand = new Random();
        try {
            Thread.sleep(rand.nextInt(STORED_MAX_DELAY_MS + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.mc.sendMessage(message);
    }

    public static void getchunk(String version, ChunkId chunk_id) throws IOException {
        ProtocolMessage message = new ProtocolMessage("GETCHUNK", version, server.server_id,
                chunk_id.file_id, chunk_id.chunk_no, 0, null, 0);
        
        //TODO Buffer full 
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        server.mc.sendMessage(message);
        ServerState.getchunk_log(chunk_id);
    }


    public static void chunk(ChunkId chunk_id, byte[] bytes, int readBytes) {

        ProtocolMessage message = new ProtocolMessage("CHUNK", server.protocol_ver, server.server_id, chunk_id.file_id, chunk_id.chunk_no, 0,
                bytes, readBytes);

        Random rand = new Random();

        try {
            Thread.sleep(rand.nextInt(CHUNK_MAX_DELAY_MS + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ServerState.is_getchunk_pendent(chunk_id)) {
            ServerState.getchunk_delete(chunk_id);
            try {
                server.mdr.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void chunk_enh(ChunkId chunk_id, byte[] bytes, int readBytes, InetAddress sender_address) {

        ProtocolMessage message = new ProtocolMessage("CHUNK", server.protocol_ver, server.server_id, chunk_id.file_id, chunk_id.chunk_no, 0,
                bytes, readBytes);

        Random rand = new Random();

        try {
            Thread.sleep(rand.nextInt(CHUNK_MAX_DELAY_MS + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TCPClient socket = new TCPClient(sender_address, Utilities.TCP_PORT);

        if (ServerState.is_getchunk_pendent(chunk_id)) {
            ServerState.getchunk_delete(chunk_id);
            try {
                socket.send(message.buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void delete(String version, String file_id) throws IOException {
    	server.protocol_ver = version;
        ProtocolMessage message = new ProtocolMessage("DELETE", version, server.server_id, file_id, 0, 0, null, 0);
        server.mc.sendMessage(message);
    }

	public static void removed(ChunkId chunk_id) throws IOException{
        ProtocolMessage message = new ProtocolMessage("REMOVED", server.protocol_ver, server.server_id, chunk_id.file_id, chunk_id.chunk_no, 0, null, 0);
        server.mc.sendMessage(message);
    }
    
	public static void lease(String file_id) throws IOException {
		ProtocolMessage message = new ProtocolMessage("LEASE", "2.0", server.server_id, file_id, 0, 0, null, 0);
		server.mc.sendMessage(message);
	}
	
	public static void leased(String file_id) throws IOException {
		ProtocolMessage message = new ProtocolMessage("LEASED", "2.0", server.server_id, file_id, 0, 0, null, 0);
		server.mc.sendMessage(message);
	}

    public static void putchunk_with_delay(ChunkId chunk_id, int replication, byte[] bytes, int readBytes) throws IOException {
        
        Random rand = new Random();

        try {
            Thread.sleep(rand.nextInt(PUTCHUNK_MAX_DELAY_MS + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int perceived_replication = ServerState.get_perceived_replication(chunk_id);
        int desired_replication = ServerState.store_get_chunk_info(chunk_id).getDesiredReplication();

        if(perceived_replication < desired_replication){
            putchunk(Utilities.STOCK_VERSION, chunk_id, replication, bytes, readBytes);
        }
    }
}
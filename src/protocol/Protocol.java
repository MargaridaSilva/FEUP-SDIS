package protocol;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import server.ChunkId;
import server.FileState;
import server.ServerInfo;

public class Protocol {

    private static final int MAX_TRIES = 5;
    private static final int BASE_WAIT_TIME = 1000; //1sec

    private static ServerInfo server = ServerInfo.getInstance();

    public static void putchunk(String file_id, int chunk_num, int replication, byte[] bytes, int readBytes)
            throws IOException {
        System.out.println("Put Chunk");

        ProtocolMessage message = new ProtocolMessage("PUTCHUNK", server.server_id, file_id, chunk_num, replication,
                bytes, readBytes);

        // TimerTask task = new TimerTask() {
        // public void run() {
        // System.out.println("Not able to replicate all\n" +
        // "Thread's name: " + Thread.currentThread().getName());
        // }
        // };
        // Timer timer = new Timer();
        // server.mdb.sendMessage(message);
        // timer.schedule(task, 400L);

        ChunkId chunk_id = new ChunkId(file_id, chunk_num);
        int tries = 0;
        int ack_num;
        int wait_time = BASE_WAIT_TIME;

        do{
            server.mdb.sendMessage(message);
            try {
                System.out.println("Going to sleep...");
                Thread.sleep(wait_time);
                System.out.println("After sleep: " + wait_time + " ms");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ack_num = FileState.get_ack_num(chunk_id);

            tries++;
            wait_time*=2;

            System.out.println("Try no. " + tries);
        }while(tries < MAX_TRIES && ack_num < replication);
    }

    public static void stored(String file_id, int chunk_num) throws IOException {
        ProtocolMessage message = new ProtocolMessage("STORED", server.server_id, file_id, chunk_num, 0, null, 0);
        server.mc.sendMessage(message);
    }

    public static void getchunk(String file_id, int chunk_num) throws IOException {
        ProtocolMessage message = new ProtocolMessage("STORED", server.server_id, file_id, chunk_num, 0, null, 0);
        server.mdb.sendMessage(message);
    }
}
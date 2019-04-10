package server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class FileState implements Serializable{
    private static final long serialVersionUID = 1L;

    public static ConcurrentHashMap<ChunkId, HashSet<Integer>> ack_chunk = new ConcurrentHashMap<>();

    public static void add_ack(ChunkId chunk_id, int server_id) {
        HashSet<Integer> servers_ack = ack_chunk.get(chunk_id);

        if(servers_ack == null){
            servers_ack = new HashSet<Integer>();
            ack_chunk.put(chunk_id, servers_ack);
        }

        servers_ack.add(server_id);
    }

    public static int get_ack_num(ChunkId chunk_id) {
        HashSet<Integer> servers_ack = ack_chunk.get(chunk_id);
        return servers_ack.size();
    }

    public static void clear_ack_count(ChunkId chunk_id){
        ack_chunk.remove(chunk_id);
    }
}
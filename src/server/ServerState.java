package server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ServerState implements Serializable{
    private static final long serialVersionUID = 1L;

    private static ConcurrentHashMap<ChunkId, HashSet<Integer>> ack_chunk = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, FileInfo> file_map = new ConcurrentHashMap<>();

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

	public static void backup_log(String filename, String file_id, int replication_deg) {
        FileInfo file_info = new FileInfo(filename, file_id, replication_deg);
        file_map.put(file_id, file_info);
	}

	public static void backup_chunk_log(String file_id, int chunk_num, int perceived_replication_deg) {
        FileInfo file_info = file_map.get(file_id);
        file_info.addChunk(chunk_num, perceived_replication_deg);
    }
    
    public static void print_backup_log(String file_id){
        System.out.println(file_map.get(file_id));
    }
}
package state;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerState implements Serializable{
    private static final long serialVersionUID = 1L;

    private static ConcurrentHashMap<ChunkId, HashSet<Integer>> ack_chunk = new ConcurrentHashMap<>();
    
    private static ConcurrentHashMap<String, FileInfo> backup_log = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ChunkId, ChunkInfo> store_log = new ConcurrentHashMap<>();
    


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
        backup_log.put(file_id, file_info);
	}

	public static void backup_chunk_log(ChunkId chunk_id) {
        FileInfo file_info = backup_log.get(chunk_id.file_id);
        file_info.addChunk(chunk_id.chunk_no, ServerState.get_ack_num(chunk_id));

        // ServerState.get_ack_num(chunk_id) -> redundancia
    }
    
    public static String backup_log_info(){

        String info = "Backup log: \n\n";

        for (FileInfo file_info : backup_log.values()) {
            info += file_info + "\n";
        }

        return info;
        
    }


	public static void store_log(ChunkId chunk_id, int size) {
        store_log.put(chunk_id, new ChunkInfo(chunk_id, size));
    }
      
    public static String store_log_info(){
        
        String info = "Store log: \n\n";

        for (ChunkInfo chunk_info : store_log.values()) {
            info += chunk_info + "\n";
        }

        return info;
        
    }
}
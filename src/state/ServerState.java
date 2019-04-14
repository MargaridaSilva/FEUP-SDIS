package state;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerState implements Serializable{
    private static final long serialVersionUID = 1L;

    //chunk store confirmation
    private static ConcurrentHashMap<ChunkId, HashSet<Integer>> perceived_replication = new ConcurrentHashMap<>();
    
    //Chunk requests this server receives
    private static Set<ChunkId> pending_getchunk = ConcurrentHashMap.newKeySet();



    //file whose backup this server has initiated
    private static ConcurrentHashMap<String, FileInfo> backup_log = new ConcurrentHashMap<>();

    //chunk this server stores
    private static ConcurrentHashMap<ChunkId, ChunkInfo> store_log = new ConcurrentHashMap<>();

    //chunk this server requests
    private static Set<ChunkId> getchunk_log = ConcurrentHashMap.newKeySet();
    


    public static void add_ack(ChunkId chunk_id, int server_id) {
        HashSet<Integer> servers_ack = perceived_replication.get(chunk_id);

        if(servers_ack == null){
            servers_ack = new HashSet<Integer>();
            perceived_replication.put(chunk_id, servers_ack);
        }

        servers_ack.add(server_id);
    }

    public static int get_ack_num(ChunkId chunk_id) {
        HashSet<Integer> servers_ack = perceived_replication.get(chunk_id);
        if(servers_ack != null){
            return servers_ack.size();
        }
        else{
            return 0;
        }
    }

    public static void remove_chunk(ChunkId chunk_id){
        perceived_replication.remove(chunk_id);
    }
    
    public static void remove_file(String file_id){
        perceived_replication.entrySet().removeIf(entries->entries.getKey().file_id.equals(file_id));
        store_log.entrySet().removeIf(entries->entries.getKey().file_id.equals(file_id));
    }




    public static void getchunk_request(ChunkId chunk_id){
        pending_getchunk.add(chunk_id);
    }

    public static void getchunk_delete(ChunkId chunk_id) {
        pending_getchunk.remove(chunk_id);
	}

    public static boolean is_getchunk_pendent(ChunkId chunk_id){
        return pending_getchunk.contains(chunk_id);
    }

    


	public static void backup_log(String filename, String file_id, int replication_deg, int chunk_num) {
        FileInfo file_info = new FileInfo(filename, file_id, replication_deg, chunk_num);
        backup_log.put(file_id, file_info);
    }

    public static int get_num_chunks(String file_id){
        FileInfo info = backup_log.get(file_id);
        return info.get_chunk_num();
    }

    public static String backup_log_info(){

        String info = "Backup log: \n\n";

        for (FileInfo file_info : backup_log.values()) {
            info += file_info + "\n";
        }

        return info;
    }




    public static void getchunk_log(ChunkId chunk_id){
        getchunk_log.add(chunk_id);
    }

    public static boolean getchunk_requested(ChunkId chunk_id){
        return getchunk_log.contains(chunk_id);
    }

    public static void getchunk_handled(ChunkId chunk_id) {
        getchunk_log.remove(chunk_id);
    }

    public static boolean getchunk_pendents(String file_id){
        for(ChunkId chunk_id : getchunk_log){
            if(chunk_id.file_id == file_id){
                return true;
            }
        }
        return false;
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
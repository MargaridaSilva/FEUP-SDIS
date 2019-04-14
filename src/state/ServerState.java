package state;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ServerState implements Externalizable{
    private static final long serialVersionUID = 1L;

    public static int max_space = Integer.MAX_VALUE;

    //chunk store confirmation
    private static ConcurrentHashMap<ChunkId, HashSet<Integer>> perceived_replication = new ConcurrentHashMap<>();
    
    //Chunk requests this server receives
    private static Set<ChunkId> pending_getchunk = ConcurrentHashMap.newKeySet();

    //file whose backup this server has initiated
    private static ConcurrentHashMap<String, FileInfo> backup_log = new ConcurrentHashMap<>();

    //chunk this server stores
    private static ConcurrentHashMap<ChunkId, ChunkInfo> store_log = new ConcurrentHashMap<>();

    //chunk this server requests
    private static  Set<ChunkId> getchunk_log = ConcurrentHashMap.newKeySet();
    


    //---------------------
    //Perceived Replication
    //---------------------

    public static void add_ack(ChunkId chunk_id, int server_id) {
        HashSet<Integer> servers_ack = perceived_replication.get(chunk_id);

        if(servers_ack == null){
            servers_ack = new HashSet<Integer>();
            perceived_replication.put(chunk_id, servers_ack);
        }

        servers_ack.add(server_id);
    }

    public static void decrease_chunk_replication(ChunkId chunk_id, int server_id){
        HashSet<Integer> servers_ack = perceived_replication.get(chunk_id);
        
        if(servers_ack != null){
            servers_ack.remove(server_id);
        }
    }


    public static int get_perceived_replication(ChunkId chunk_id) {
        HashSet<Integer> servers_ack = perceived_replication.get(chunk_id);
        if(servers_ack != null){
            return servers_ack.size();
        }
        else{
            return 0;
        }
    }
    
    public static void remove_file(String file_id){
        perceived_replication.entrySet().removeIf(entries->entries.getKey().file_id.equals(file_id));
        store_log.entrySet().removeIf(entries->entries.getKey().file_id.equals(file_id));
    }







    //---------------------
    // Pending getchunks
    //---------------------

    public static void getchunk_request(ChunkId chunk_id){
        pending_getchunk.add(chunk_id);
    }

    public static void getchunk_delete(ChunkId chunk_id) {
        pending_getchunk.remove(chunk_id);
	}

    public static boolean is_getchunk_pendent(ChunkId chunk_id){
        return pending_getchunk.contains(chunk_id);
    }

    





    //------------
    // Backup log
    //------------

	public static void backup_log(String filename, String file_id, int replication_deg, int chunk_num) {
        FileInfo file_info = new FileInfo(filename, file_id, replication_deg, chunk_num);
        backup_log.put(file_id, file_info);
    }

    public static boolean backup_initiator(String file_id){
        return backup_log.containsKey(file_id);
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






    //--------------
    // Getchunk log
    //--------------

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



    


    //------------
    // Store log
    //------------

	public static void store_log(ChunkId chunk_id, int size, int desired_replication) {
        store_log.put(chunk_id, new ChunkInfo(chunk_id, size, desired_replication));
    }

    public static PriorityQueue<ChunkInfo> get_stored_chunks(){
        PriorityQueue<ChunkInfo> priority_queue = new PriorityQueue<>();

        for(ChunkInfo chunk_info : store_log.values()){
            priority_queue.add(chunk_info);
        }

        return priority_queue;
    }

    public static ChunkInfo store_get_chunk_info(ChunkId chunk_id) {
        return store_log.get(chunk_id);
    }

    public static int get_used_space(){
        int size = 0;

        for(ChunkInfo chunk_info : store_log.values()){
            size += chunk_info.getSize();
        }

        return size;
    }

	public static boolean store_contain_chunk(ChunkId chunk_id) {
		return store_log.containsKey(chunk_id);
	}

    public static void remove_stored_chunk(ChunkId chunk_id){
        store_log.remove(chunk_id);
    }
      
    public static String store_log_info(){
        
        String info = "Store log: \n\n";

        for (ChunkInfo chunk_info : store_log.values()) {
            info += chunk_info + "\n";
        }

        return info;
        
    }

   

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		perceived_replication = (ConcurrentHashMap<ChunkId, HashSet<Integer>>) in.readObject();
		pending_getchunk =  (Set<ChunkId>) in.readObject();
		backup_log =  (ConcurrentHashMap<String, FileInfo>) in.readObject();
		store_log =  (ConcurrentHashMap<ChunkId, ChunkInfo>) in.readObject();
		getchunk_log =  (Set<ChunkId>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(perceived_replication);
		out.writeObject(pending_getchunk);
		out.writeObject(backup_log);
		out.writeObject(store_log);
		out.writeObject(getchunk_log);
		
	}
}
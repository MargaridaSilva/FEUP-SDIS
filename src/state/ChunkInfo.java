package state;

import utilities.Utilities;

public class ChunkInfo implements Comparable<ChunkInfo> {

    private ChunkId id;
    private int size;
    private int desired_replication;

    ChunkInfo(ChunkId id, int size, int desired_replication) {
        this.id = id;
        this.size = size;
        this.desired_replication = desired_replication;
    }

    public int getSize(){
        return this.size;
    }
    private int weight(){
        int delta_replication = ServerState.get_perceived_replication(id) - desired_replication;
        return delta_replication*Utilities.CHUNK_SIZE + size;
    }
    public ChunkId getChunkId(){
        return this.id;
    }

    public int getDesiredReplication() {
        return desired_replication;
    }

    @Override
    public int compareTo(ChunkInfo arg) {
        int dv = this.weight() - arg.weight();
        return (dv < 0) ? -1 : (dv > 0) ? 1 : 0 ;
    }

    @Override
    public String toString() {
        String to_string = 
            "File id: " + id.file_id + "\n" + 
            "Chunk no.: " + id.chunk_no + "\n" + 
            "Size no.: " + (double) size/1000 + " KByte\n" + 
            "Desired relication degree: " + desired_replication + "\n"+
            "Perceived replication degree: " + ServerState.get_perceived_replication(id) + "\n"+
            "------------------------------------------";

        return to_string;
    }

 
}

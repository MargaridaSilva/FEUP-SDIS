package state;

public class ChunkInfo {

    private ChunkId id;
    private int size;

    ChunkInfo(ChunkId id, int size) {
        this.id = id;
        this.size = size;
    }

    @Override
    public String toString() {
        String to_string = 
            "File id: " + id.file_id + "\n" + 
            "Chunk no.: " + id.chunk_no + "\n" + 
            "Size no.: " + size + " KByte\n" + 
            "Perceived replication degree: " + ServerState.get_ack_num(id) + "\n"+
            "------------------------------------------";

        return to_string;
    }
}

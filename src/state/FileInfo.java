package state;

public class FileInfo {
    private String filename;
    private String file_id;
    private int replication_deg;
    private int chunk_num;

    FileInfo(String filename, String file_id, int replication_deg, int chunk_num) {
        this.filename = filename;
        this.file_id = file_id;
        this.replication_deg = replication_deg;
        this.chunk_num = chunk_num;
    }
    
    /**
     * @return the chunk_num
     */
    public int get_chunk_num() {
        return chunk_num;
    }
    
    @Override
    public String toString() {
        String to_string = 
            "Filename: " + filename + "\n" +
            "File id: " + file_id + "\n" + 
            "Desired replication degree: " + replication_deg + "\n"+
            "Chunks: " + "\n";


        to_string += "Chunk no." + "\t" + "Perceived replication" + "\n";
        for (int i = 0; i < chunk_num; i++) {
            ChunkId chunk_id = new ChunkId(file_id, i);
            to_string += i + "\t\t\t" + ServerState.get_perceived_replication(chunk_id) + "\n"; 
        }

        to_string += "------------------------------------------";
        return to_string;
    }
}

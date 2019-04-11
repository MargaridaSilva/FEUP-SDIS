package state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileInfo {
    private String filename;
    private String file_id;
    private int replication_deg;
    private String pathname;
    private ConcurrentHashMap<Integer, Integer> chunk_replication;

    FileInfo(String filename, String file_id, int replication_deg) {
        this.filename = filename;
        this.file_id = file_id;
        this.replication_deg = replication_deg;
        this.chunk_replication = new ConcurrentHashMap<>();
    }

    public void addChunk(int chunk_num, int perceived_replication_deg) {
        chunk_replication.put(chunk_num, perceived_replication_deg);
    }

    @Override
    public String toString() {
        String to_string = 
            "Filename: " + filename + "\n" +
            "File id: " + file_id + "\n" + 
            "Desired replication degree: " + replication_deg + "\n"+
            "Chunks: " + "\n";

        for (Map.Entry<Integer, Integer> entry : chunk_replication.entrySet()) {
            int chunk_num = entry.getKey();
            int perceived_replication_deg = entry.getValue();
            to_string += chunk_num + "\t" + perceived_replication_deg + "\n"; 
        }
        return to_string;
    }
}

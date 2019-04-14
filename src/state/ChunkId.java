package state;

public class ChunkId {
    public String file_id;
    public int chunk_no;

    public ChunkId(String file_id, int chunk_no){
        this.file_id = file_id;
        this.chunk_no = chunk_no;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.file_id.hashCode();
        result = 31 * result + this.chunk_no;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ChunkId)) {
            return false;
        }

        ChunkId id = (ChunkId) obj;

        return this.file_id.equals(id.file_id) 
                && this.chunk_no == id.chunk_no;
    }

    @Override
    public String toString() {
        return "File id: " + this.file_id + "\n" +
                "Chunk no.: " + this.chunk_no;
    }
}
package server;

public class ChunkId {
    String fileId;
    int chunk_no;

    public ChunkId(String fileId, int chunk_no){
        this.fileId = fileId;
        this.chunk_no = chunk_no;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.fileId.hashCode();
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

        return this.fileId.equals(id.fileId) 
                && this.chunk_no == id.chunk_no;
    }
}
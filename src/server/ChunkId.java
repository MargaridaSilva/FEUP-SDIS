package server;

class ChunkId {
    String fileId;
    int chunk_no;

    ChunkId(String fileId, int chunk_no){
        this.fileId = fileId;
        this.chunk_no = chunk_no;
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
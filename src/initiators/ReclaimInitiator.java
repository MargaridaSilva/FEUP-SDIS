package initiators;

import java.io.IOException;
import java.util.List;

import protocol.Protocol;
import server.Algorithm;
import state.ChunkInfo;
import state.ServerState;
import utilities.Utilities;

public class ReclaimInitiator implements Runnable {
	int max_space;
	
	public ReclaimInitiator(int max_space) {
		this.max_space = max_space;
	}
		
	@Override
	public void run() {
		ServerState.max_space = max_space;
        
        List<ChunkInfo> chunks_to_remove = Algorithm.chunks_to_remove(ServerState.get_stored_chunks(), Utilities.kbyte_to_byte(max_space));

        for(ChunkInfo chunk_info : chunks_to_remove){
            ServerState.remove_stored_chunk(chunk_info.getChunkId());
            try {
                Protocol.removed(chunk_info.getChunkId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
}
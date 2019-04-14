package initiators;

import protocol.Protocol;
import state.ChunkId;
import state.ServerState;
import utilities.FileSystem;
import utilities.Utilities;

public class RestoreInitiator implements Runnable {
	String filename;
	String version;
	
	public RestoreInitiator(String filename){
		this.filename = filename;
		this.version = Utilities.STOCK_VERSION;
	}


	protected void run_common(){
		try {
            String file_id = Utilities.generateIdentifier(Utilities.FILES_DIR + filename);
            int num_chunks = ServerState.get_num_chunks(file_id);

			for(int i = 0; i < num_chunks; i++) {
				Protocol.getchunk(version, new ChunkId(file_id, i));
            }
            int i = 0;

            while(ServerState.getchunk_pendents(file_id) && i < 10){
                Thread.sleep(100);
                i++;
			}

            if(!ServerState.getchunk_pendents(file_id)){
                FileSystem.getInstance().join_chunk(file_id, filename);
            }else{
				System.out.println("ERROR: Server wasn't able to recover all chunks");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.run_common();
	}
}
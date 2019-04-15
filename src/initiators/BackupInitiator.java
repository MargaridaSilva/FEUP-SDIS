package initiators;

import java.io.FileInputStream;
import java.io.InputStream;

import protocol.Protocol;
import state.ChunkId;
import state.ServerState;
import utilities.Utilities;

public class BackupInitiator implements Runnable {

	String filename;
    int replication;
    String version;
	
	public BackupInitiator(String filename, int replication){
		this.filename = filename;
        this.replication = replication;
        this.version = Utilities.STOCK_VERSION;
	}
    
	@Override
	public void run() {
		
		
        try{
            InputStream in_file = new FileInputStream(Utilities.FILES_DIR + filename);
            String file_id = Utilities.generateIdentifier(Utilities.FILES_DIR + filename);

            int readBytes = 0;
            byte[] bytes = new byte[Utilities.CHUNK_SIZE];
            int i = 0;

            while ((readBytes = in_file.read(bytes, 0, Utilities.CHUNK_SIZE)) != -1) {
                ChunkId chunk_id = new ChunkId(file_id, i);
                if (this.version.equals(Utilities.ENH_VERSION) && ServerState.get_perceived_replication(chunk_id) >= replication)
                	continue;
                Protocol.putchunk(version, chunk_id, replication, bytes, readBytes);
                i++;
            }

            in_file.close();

            ServerState.backup_log(filename, file_id, replication, i);

        }catch(Exception e){
            e.printStackTrace();
        }
	}
	
}
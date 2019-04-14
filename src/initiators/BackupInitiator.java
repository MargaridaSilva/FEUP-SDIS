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
	boolean enh;
	
	public BackupInitiator(String filename, int replication, boolean enh){
		this.filename = filename;
		this.replication = replication;
		this.enh = enh;
	}
	
	@Override
	public void run() {
	 try{
            InputStream in_file = new FileInputStream(Utilities.FILES_DIR + filename);
            String file_id = Utilities.generateIdentifier(Utilities.FILES_DIR + filename);

            int readBytes = 0;
            byte[] bytes = new byte[Utilities.CHUNK_SIZE];
            int i = 0;
            Protocol.set_version(this.enh);

            while ((readBytes = in_file.read(bytes, 0, Utilities.CHUNK_SIZE)) != -1) {
                ChunkId chunk_id = new ChunkId(file_id, i);
                Protocol.putchunk(chunk_id, replication, bytes, readBytes);
                i++;
            }

            in_file.close();

            ServerState.backup_log(filename, file_id, replication, i);

        }catch(Exception e){
            e.printStackTrace();
        }
		
	}
	
}
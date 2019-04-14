package state;
import java.util.concurrent.TimeUnit;

import utilities.FileSystem;
import utilities.Utilities;

public class ServerBackup implements Runnable {
	
	ServerState st = new ServerState();
	public static String path;
	
	public ServerBackup(int server_id) {
		path = Utilities.SERVER_BACKUP_DIR + server_id;
        FileSystem.getInstance().createServerBackupStructure();
	}
		
	@Override
	public void run() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileSystem.getInstance().save_server_state(st);
		}
		
	}	
}
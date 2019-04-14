package state;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import utilities.FileSystem;
import utilities.Utilities;

public class ServerBackup implements Runnable {
	
	ServerState st = new ServerState();
	
	public ServerBackup() {
		String filepath = Utilities.SERVER_BACKUP_DIR;
        if (Files.notExists(Paths.get(filepath))) {
            try {
                Files.createDirectories(Paths.get(filepath));
            } catch (IOException e) {
                e.printStackTrace();
            }
       }
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